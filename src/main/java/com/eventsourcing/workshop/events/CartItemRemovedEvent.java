package com.eventsourcing.workshop.events;

import java.util.UUID;

public record CartItemRemovedEvent(
        UUID itemId) implements CartEvent {
    @Override
    public StoreEventTypes getEventType() {
        return StoreEventTypes.CartItemRemoved;
    }
}
