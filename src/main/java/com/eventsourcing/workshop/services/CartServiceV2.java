package com.eventsourcing.workshop.services;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.clients.StorageClient;
import com.eventsourcing.workshop.events.*;
import com.eventsourcing.workshop.models.Bucket;
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
        // Fetch the cart from the database
        Cart cart = storageClient.get(Bucket.CARTS, id).orElse(new Cart(id));

        // Apply the event to the cart
        cart.applyEvent(event);

        // Save the cart to the database
        storageClient.put(Bucket.CARTS, id, cart);
    }

    // Part 3
    @Override
    public Optional<Cart> getCart(String id) {
        // Fetch the cart from the database
        return storageClient.get(Bucket.CARTS, id);
    }
}
