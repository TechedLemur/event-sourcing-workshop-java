package com.eventsourcing.workshop.events;

public sealed interface ProductEvent extends StoreEventData
        permits ProductCreatedEvent, ProductPriceUpdatedEvent, ProductDetailsUpdatedEvent {
}
