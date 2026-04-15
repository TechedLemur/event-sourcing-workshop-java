package com.eventsourcing.workshop.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.eventsourcing.workshop.events.CartEvent;
import com.eventsourcing.workshop.events.CartItemAddedEvent;
import com.eventsourcing.workshop.events.CartItemAddedV2Event;
import com.eventsourcing.workshop.events.CartItemRemovedEvent;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Cart {
    private String id;
    private List<CartItem> items;
    private double total;

    // This constructor is needed for deserializing from local json file
    @SuppressWarnings("unused")
    private Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(String id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.total = 0;
    }

    // Part 2
    public Cart applyEvent(CartEvent event) {
        switch (event) {
            case CartItemAddedEvent cartItemAddedEvent -> {
                // Add the item to the cart
                CartItem newItem = new CartItem(cartItemAddedEvent.itemId(), cartItemAddedEvent.productId(),
                        cartItemAddedEvent.productName(), cartItemAddedEvent.productPrice(), null);

                this.items.add(newItem);

            }
            case CartItemRemovedEvent cartItemRemovedEvent -> {
                // Remove the item from the cart
                this.items.removeIf(item -> item.id().equals(cartItemRemovedEvent.itemId()));
            }

            default -> {
                throw new IllegalArgumentException("Unknown event type: " +
                        event.getClass().getName());
            }

        }

        this.total = calculateTotal();
        return this;
    }

    // Part 2
    private double calculateTotal() {
        // TODO: instead of returning 0, return the total price of the items in the cart
        return 0;
    }

    public String getId() {
        return id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}
