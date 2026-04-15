package com.eventsourcing.workshop.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.eventsourcing.workshop.events.ProductCreatedEvent;
import com.eventsourcing.workshop.events.ProductDetailsUpdatedEvent;
import com.eventsourcing.workshop.events.ProductEvent;
import com.eventsourcing.workshop.events.ProductPriceUpdatedEvent;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Product {
    private String id;
    private String name;
    private double price;
    private String currency;
    private String description;
    private String imageUrl;

    @SuppressWarnings("unused")
    private Product() {
    }

    public Product(String id) {
        this.id = id;
    }

    public Product(String id, String name, double price, String currency, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Product applyEvent(ProductEvent event) {
        switch (event) {
            case ProductCreatedEvent productCreatedEvent -> {
                this.name = productCreatedEvent.name();
                this.price = productCreatedEvent.price();
                this.currency = productCreatedEvent.currency();
                this.description = productCreatedEvent.description();
                this.imageUrl = productCreatedEvent.imageUrl();
            }
            case ProductPriceUpdatedEvent productPriceUpdatedEvent -> {
                this.price = productPriceUpdatedEvent.price();
                this.currency = productPriceUpdatedEvent.currency();
            }
            case ProductDetailsUpdatedEvent productDetailsUpdatedEvent -> {
                this.name = productDetailsUpdatedEvent.name();
                this.description = productDetailsUpdatedEvent.description();
                this.imageUrl = productDetailsUpdatedEvent.imageUrl();
            }
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}