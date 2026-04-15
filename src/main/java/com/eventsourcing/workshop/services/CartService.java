package com.eventsourcing.workshop.services;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.events.CartEvent;
import com.eventsourcing.workshop.events.CartItemAddedEvent;
import com.eventsourcing.workshop.events.CartItemAddedV2Event;
import com.eventsourcing.workshop.events.CartItemRemovedEvent;
import com.eventsourcing.workshop.events.StoreEvent;
import com.eventsourcing.workshop.models.Cart;
import com.eventsourcing.workshop.models.PriceWithCurrency;
import com.eventsourcing.workshop.models.Product;

import io.kurrent.dbclient.ReadStreamOptions;

@Component
public class CartService {
    protected final EventClient eventClient;
    protected final ProductService productService;
    private final Logger logger = Logger.getLogger(CartService.class.getName());

    public static final String CART_AGGREGATION_STREAM = "$ce-cart";

    public static String mapStreamName(String id) {
        return "cart-%s".formatted(id);
    }

    public CartService(EventClient eventClient, ProductService productService) {
        this.eventClient = eventClient;
        this.productService = productService;
    }

    // Part 1
    public void addItemToCart(String cartId, String productId) {

        Optional<Product> optionalProduct = Optional.empty();
        // Fetch the product information by productId
        optionalProduct = productService.getProduct(productId);

        UUID itemId = UUID.randomUUID();

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            CartItemAddedV2Event cartItemAddedEvent = null;
            // Create a new CartItemAddedEvent
            cartItemAddedEvent = new CartItemAddedV2Event(itemId, product.getId(), product.getName(),
                    new PriceWithCurrency(product.getPrice(), product.getCurrency()));

            eventClient.emit(mapStreamName(cartId), cartItemAddedEvent.toStoreEvent(cartId));
        } else {
            logger.warning("Product not found for productId: " + productId);
        }

    }

    // Part 1
    public void removeItemFromCart(String cartId, UUID itemId) {
        CartItemRemovedEvent cartItemRemovedEvent = null;
        // Create a new CartItemRemovedEvent
        cartItemRemovedEvent = new CartItemRemovedEvent(itemId);

        eventClient.emit(mapStreamName(cartId), cartItemRemovedEvent.toStoreEvent(cartId));
    }

    public Optional<Cart> getCart(String id) {
        return getCart(id, Long.MAX_VALUE);
    }

    public Optional<Cart> getCart(String id, long maxCount) {
        Cart cart = new Cart(id);
        if (maxCount <= 0) {
            return Optional.empty();
        }
        // https://docs.kurrent.io/clients/java/v1.1/reading-events.html#maxcount-1
        try {
            eventClient.read(mapStreamName(id), CartEvent.class, ReadStreamOptions.get().maxCount(maxCount))
                    .forEach(cart::applyEvent);
            return Optional.of(cart);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getCartLastEventRevision(String id) {
        try {
            return eventClient.read(mapStreamName(id), ReadStreamOptions.get().fromEnd().backwards().maxCount(1))
                    .findFirst()
                    .map(StoreEvent::revision)
                    .orElse(null);
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

}
