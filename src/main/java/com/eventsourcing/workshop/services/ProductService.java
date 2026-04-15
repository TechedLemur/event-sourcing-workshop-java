package com.eventsourcing.workshop.services;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.events.ProductCreatedEvent;
import com.eventsourcing.workshop.events.ProductEvent;
import com.eventsourcing.workshop.events.ProductPriceUpdatedEvent;
import com.eventsourcing.workshop.models.Product;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Component
public class ProductService {
    protected final EventClient eventClient;

    public ProductService(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    public static final String PRODUCT_AGGREGATION_STREAM = "$ce-product";

    public static String mapStreamName(String id) {
        return "product-%s".formatted(id);
    }

    public void createProduct(Product product) {
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCurrency());

        eventClient.emit(mapStreamName(product.getId()), productCreatedEvent.toStoreEvent(product.getId()));
    }

    public Collection<Product> getAllProducts() {
        Map<String, Product> productsMap = new HashMap<>();
        try {
            eventClient.read(PRODUCT_AGGREGATION_STREAM).forEach(event -> {
                String productId = event.subject();
                ProductEvent productEvent = (ProductEvent) event.data(); // Safe cast since we only read ProductEvents
                Product product = productsMap.getOrDefault(productId, new Product(productId));

                product.applyEvent(productEvent);

                productsMap.put(productId, product);
            });
            return productsMap.values();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Product> getProduct(String id) {
        Product product = new Product(id);
        try {
            var events = eventClient.read(mapStreamName(id), ProductEvent.class).toList();
            if (events.isEmpty()) {
                return Optional.empty();
            }
            events.forEach(product::applyEvent);
            return Optional.of(product);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProductPrice(String id, double price, String currency) {
        ProductPriceUpdatedEvent productPriceUpdatedEvent = new ProductPriceUpdatedEvent(price, currency);
        eventClient.emit(mapStreamName(id), productPriceUpdatedEvent.toStoreEvent(id));
    }
}
