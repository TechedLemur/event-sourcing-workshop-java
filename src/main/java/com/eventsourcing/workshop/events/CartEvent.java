package com.eventsourcing.workshop.events;

public sealed interface CartEvent extends StoreEventData
        permits CartItemAddedEvent, CartItemRemovedEvent, CartItemAddedV2Event {

}
