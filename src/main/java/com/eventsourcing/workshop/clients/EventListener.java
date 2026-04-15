package com.eventsourcing.workshop.clients;

import com.eventsourcing.workshop.events.StoreEventData;
import io.kurrent.dbclient.Position;
import io.kurrent.dbclient.Subscription;

import java.time.Instant;

public abstract class EventListener<T extends StoreEventData> {
    /**
     * Called when EventStoreDB sends an event to the subscription.
     * 
     * @param subscription handle to the subscription.
     * @param event        a resolved event.
     */
    public void onEvent(Subscription subscription, String subject, T event, long revision) {
    }

    /**
     * Called when the subscription is cancelled or dropped.
     * 
     * @param subscription handle to the subscription.
     * @param exception    an exception. null if the user initiated the
     *                     cancellation.
     */
    public void onCancelled(Subscription subscription, Throwable exception) {
    }

    /**
     * Called when the subscription is confirmed by the server.
     * 
     * @param subscription handle to the subscription.
     */
    public void onConfirmation(Subscription subscription) {
    }

    /**
     * Called when the subscription has reached the head of the stream.
     * 
     * @param subscription handle to the subscription.
     */
    public void onCaughtUp(Subscription subscription, Instant timestamp, Long streamRevision, Position position) {
    }

    /**
     * Called when the subscription has fallen behind, meaning it's no longer
     * keeping up with the
     * stream's pace.
     * 
     * @param subscription handle to the subscription.
     */
    public void onFellBehind(Subscription subscription, Instant timestamp, Long streamRevision, Position position) {
    }
}
