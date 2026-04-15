package com.eventsourcing.workshop.services;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.clients.StorageClient;
import com.eventsourcing.workshop.events.*;
import com.eventsourcing.workshop.models.Cart;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CartServiceV2 extends CartService {
    private final StorageClient storageClient;

    public CartServiceV2(EventClient eventClient, ProductServiceV2 productService, StorageClient storageClient) {
        super(eventClient, productService);
        this.storageClient = storageClient;
    }

    // Part 3
    public void handleCartEvent(String id, CartEvent event) {
        // TODO: Fetch the cart from the database
        // Cart cart = ...

        // TODO: Apply the event to the cart

        // TODO: Save the cart to the database
    }

    // Part 3
    @Override
    public Optional<Cart> getCart(String id) {
        // TODO: Fetch the cart from the database instead of returning empty

        return Optional.empty();
    }
}
