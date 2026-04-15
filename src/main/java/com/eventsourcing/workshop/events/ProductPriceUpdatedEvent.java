package com.eventsourcing.workshop.events;

public record ProductPriceUpdatedEvent(
        double price,
        String currency)
        implements ProductEvent {
    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.ProductPriceUpdated;
    }
}
