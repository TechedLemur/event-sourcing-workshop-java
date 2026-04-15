package com.eventsourcing.workshop.events;

public record HelloEvent(String message) implements StoreEventData {
    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.Hello;
    }

}
