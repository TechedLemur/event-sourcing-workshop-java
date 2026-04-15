package com.eventsourcing.workshop.events;

public sealed interface StoreEventData permits CartEvent, ProductEvent, HelloEvent {
    /// The type of event
    StoreEventTypes getEventType();

    default StoreEvent toStoreEvent(String subject) {
        return new StoreEvent(subject, this);
    }
}
