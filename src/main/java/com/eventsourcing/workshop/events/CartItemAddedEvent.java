package com.eventsourcing.workshop.events;

import java.util.UUID;

public record CartItemAddedEvent(
        UUID itemId,
        String productId,
        String productName,
        double productPrice) implements CartEvent {

    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.CartItemAdded;
    }
}
