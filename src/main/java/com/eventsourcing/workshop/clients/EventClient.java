package com.eventsourcing.workshop.clients;

import com.eventsourcing.workshop.events.*;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.kurrent.dbclient.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class EventClient {

    private final KurrentDBClient client;
    private final JsonMapper jsonMapper;

    public EventClient(@Value("${kurrentdb.connection-string}") String connectionString) {
        KurrentDBClientSettings settings = KurrentDBConnectionString
                .parseOrThrow(connectionString);
        this.client = KurrentDBClient.create(settings);
        this.jsonMapper = new JsonMapper();
    }

    private EventData mapStoreEventToEventData(StoreEvent event) throws EventSerializationException {

        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(event.getEventType().toString())
                .withSource(URI.create("es-workshop"))
                .withData(this.jsonMapper.writeValueAsBytes(event.data()))
                .withSubject(event.subject())
                .build();

        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);
        // Not optimal ofc, but we take some liberties in this workshop
        EventFormat resolvedFormat = Objects.requireNonNull(format, "JsonFormat must be registered");
        CloudEvent nonNullEvent = Objects.requireNonNull(cloudEvent, "CloudEvent build failed");
        return EventData.builderAsJson(event.getEventType().toString(), resolvedFormat.serialize(nonNullEvent))
                .build();
    }

    private StoreEvent mapResolvedEventToStoreEvent(ResolvedEvent event) {
        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);
        // Not optimal ofc, but we take some liberties in this workshop
        EventFormat resolvedFormat = Objects.requireNonNull(format, "JsonFormat must be registered");

        RecordedEvent originalEvent = event.getEvent();
        System.out.println("Original event: " + originalEvent);
        String eventType = originalEvent.getEventType();

        byte[] eventData = Objects.requireNonNull(originalEvent.getEventData(), "Event data must be present");
        CloudEvent cloudEvent = resolvedFormat.deserialize(eventData);

        StoreEventData storeEventData = this.jsonMapper.readValue(cloudEvent.getData().toBytes(),
                StoreEventDataTypesClassResolver.classFor(eventType));

        return new StoreEvent(cloudEvent.getSubject(), storeEventData, originalEvent.getRevision());
    }

    private Iterator<EventData> toStoreEvents(Iterable<StoreEvent> src) {
        return StreamSupport.stream(src.spliterator(), false)
                .map(this::mapStoreEventToEventData)
                .iterator();
    }

    public void emit(String streamName, Iterable<StoreEvent> events) {
        client.appendToStream(streamName, toStoreEvents(events));
    }

    public void emit(String streamName, StoreEvent... events) {
        client.appendToStream(streamName, toStoreEvents(Arrays.stream(events).toList()));
    }

    public Stream<StoreEvent> read(String streamName, ReadStreamOptions options)
            throws ExecutionException, InterruptedException {
        try {
            ReadResult result = client.readStream(streamName, options.resolveLinkTos())
                    .get();
            return result.getEvents().stream().map(this::mapResolvedEventToStoreEvent);
        } catch (ExecutionException e) {
            // If stream doesn't exist yet, assume there are no events
            Throwable innerException = e.getCause();
            if (innerException instanceof StreamNotFoundException) {
                return Stream.empty();
            }
            throw e;
        }
    }

    public Stream<StoreEvent> read(String streamName) throws ExecutionException, InterruptedException {
        return read(streamName, ReadStreamOptions.get().forwards());
    }

    public <E extends StoreEventData> Stream<E> read(String streamName, Class<E> eventType)
            throws ExecutionException, InterruptedException {
        return read(streamName)
                .filter(e -> eventType.isInstance(e.data()))
                .map(e -> eventType.cast(e.data()));
    }

    public <E extends StoreEventData> Stream<E> read(String streamName, Class<E> eventType, ReadStreamOptions options)
            throws ExecutionException, InterruptedException {
        return read(streamName, options)
                .filter(e -> eventType.isInstance(e.data()))
                .map(e -> eventType.cast(e.data()));
    }

    public <E extends StoreEventData> void subscribe(String streamName, Class<E> eventType, EventListener<E> listener,
            SubscribeToStreamOptions options) {
        SubscriptionListener listenerWrapper = new SubscriptionListener() {
            @Override
            public void onEvent(Subscription subscription, ResolvedEvent event) {
                StoreEvent storeEvent = mapResolvedEventToStoreEvent(event);
                if (eventType.isInstance(storeEvent.data())) {

                    listener.onEvent(subscription, storeEvent.subject(), eventType.cast(storeEvent.data()),
                            event.getLink().getRevision());
                }
            }

            @Override
            public void onCancelled(Subscription subscription, Throwable exception) {
                listener.onCancelled(subscription, exception);
            }

            @Override
            public void onConfirmation(Subscription subscription) {
                listener.onConfirmation(subscription);
            }

            @Override
            public void onCaughtUp(Subscription subscription, Instant timestamp, Long streamRevision,
                    Position position) {
                listener.onCaughtUp(subscription, timestamp, streamRevision, position);
            }

            @Override
            public void onFellBehind(Subscription subscription, Instant timestamp, Long streamRevision,
                    Position position) {
                listener.onFellBehind(subscription, timestamp, streamRevision, position);
            }
        };

        client.subscribeToStream(streamName, listenerWrapper, options.resolveLinkTos());
    }

}
