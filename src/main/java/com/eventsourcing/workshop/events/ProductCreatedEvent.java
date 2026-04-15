package com.eventsourcing.workshop.events;

public record ProductCreatedEvent(
        String name,
        double price,
        String description,
        String imageUrl,
        String currency
) implements ProductEvent {
    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.ProductCreated;
    }

}
