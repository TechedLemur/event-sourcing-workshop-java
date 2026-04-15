package com.eventsourcing.workshop.events;

import java.util.UUID;

import com.eventsourcing.workshop.models.PriceWithCurrency;

public record CartItemAddedV2Event(
        UUID itemId,
        String productId,
        String productName,
        PriceWithCurrency productPrice) implements CartEvent {

    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.CartItemAddedV2;
    }
}
