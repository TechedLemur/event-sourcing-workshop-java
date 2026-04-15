package com.eventsourcing.workshop.models;

public final class Bucket<T> {
    public static final Bucket<Cart> CARTS = new Bucket<>("carts", Cart.class);
    public static final Bucket<Product> PRODUCTS = new Bucket<>("products", Product.class);
    public static final Bucket<Long> CHECKPOINTS = new Bucket<>("checkpoints", Long.class);

    private final String key;
    private final Class<T> type;

    private Bucket(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public Class<T> getType() {
        return type;
    }
}