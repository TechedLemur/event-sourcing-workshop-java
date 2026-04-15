package com.eventsourcing.workshop.events;

public record ProductDetailsUpdatedEvent(
        String name,
        String description,
        String imageUrl) implements ProductEvent {
    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.ProductDetailsUpdated;
    }
}
