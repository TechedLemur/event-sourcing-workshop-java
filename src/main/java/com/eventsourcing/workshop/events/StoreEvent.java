package com.eventsourcing.workshop.events;

public record StoreEvent(String subject, StoreEventData data, long revision) {
    public StoreEvent(String subject, StoreEventData data) {
        this(subject, data, -1L);
    }

    public StoreEventTypes getEventType() {
        return data.getEventType();
    }
}
