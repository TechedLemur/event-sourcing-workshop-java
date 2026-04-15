package com.eventsourcing.workshop.services;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.clients.StorageClient;
import com.eventsourcing.workshop.events.ProductEvent;
import com.eventsourcing.workshop.models.Bucket;
import com.eventsourcing.workshop.models.Product;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class ProductServiceV2 extends ProductService {
    private final StorageClient storageClient;

    public ProductServiceV2(EventClient eventClient, StorageClient storageClient) {
        super(eventClient);
        this.storageClient = storageClient;
    }

    public void handleProductEvent(String id, ProductEvent event) {
        Product product = storageClient.get(Bucket.PRODUCTS, id).orElse(new Product(id));
        product.applyEvent(event);
        storageClient.put(Bucket.PRODUCTS, id, product);
    }

    @Override
    public Collection<Product> getAllProducts() {
        return storageClient.list(Bucket.PRODUCTS);
    }

    @Override
    public Optional<Product> getProduct(String id) {
        return storageClient.get(Bucket.PRODUCTS, id);
    }
}
