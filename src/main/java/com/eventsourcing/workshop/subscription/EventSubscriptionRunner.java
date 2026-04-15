package com.eventsourcing.workshop.subscription;

import com.eventsourcing.workshop.clients.EventClient;
import com.eventsourcing.workshop.clients.EventListener;
import com.eventsourcing.workshop.clients.StorageClient;
import com.eventsourcing.workshop.events.CartEvent;
import com.eventsourcing.workshop.events.ProductEvent;
import com.eventsourcing.workshop.models.Bucket;
import com.eventsourcing.workshop.services.CartService;
import com.eventsourcing.workshop.services.CartServiceV2;
import com.eventsourcing.workshop.services.ProductServiceV2;
import io.kurrent.dbclient.SubscribeToStreamOptions;
import io.kurrent.dbclient.Subscription;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class EventSubscriptionRunner implements ApplicationRunner {
    private final StorageClient storageClient;
    private final EventClient eventClient;
    private final ProductServiceV2 productServiceV2;
    private final CartServiceV2 cartServiceV2;

    private static final Logger logger = Logger.getLogger(EventSubscriptionRunner.class.getName());

    public EventSubscriptionRunner(StorageClient storageClient, EventClient eventClient,
            ProductServiceV2 productServiceV2, CartServiceV2 cartServiceV2) {
        this.storageClient = storageClient;
        this.eventClient = eventClient;
        this.productServiceV2 = productServiceV2;
        this.cartServiceV2 = cartServiceV2;
    }

    private void startProductSubscription() {
        StorageClient storageWrapper = this.storageClient;

        Optional<Long> checkpoint = Optional.empty();
        // TODO: Fetch the checkpoint from the database
        // checkpoint = ...

        logger.info("Starting product subscription from checkpoint %s".formatted(checkpoint));
        if (checkpoint.isEmpty()) {
            logger.info("No checkpoint found for products bucket, clearing bucket to ensure db state is consistent");
            storageClient.clearBucket(Bucket.PRODUCTS);
        }
        EventListener<ProductEvent> listener = new EventListener<>() {
            @Override
            public void onEvent(Subscription subscription, String subject, ProductEvent event, long revision) {
                logger.info("New product event gotten %s %s%n".formatted(revision, event));
                productServiceV2.handleProductEvent(subject, event);

                // TODO: Save the checkpoint to the database
            }
        };

        SubscribeToStreamOptions options = SubscribeToStreamOptions.get().fromStart();
        // TODO: Set the fromRevision parameter if a checkpoint exists
        // options = ...

        this.eventClient.subscribe(ProductServiceV2.PRODUCT_AGGREGATION_STREAM, ProductEvent.class, listener, options);
    }

    private void startCartSubscription() {
        StorageClient storageWrapper = this.storageClient;

        Optional<Long> checkpoint = Optional.empty();
        // TODO: Fetch the checkpoint from the database
        // checkpoint = ...

        logger.info("Starting cart subscription from checkpoint %s".formatted(checkpoint));
        if (checkpoint.isEmpty()) {
            logger.info("No checkpoint found for carts bucket, clearing bucket to ensure db state is consistent");
            storageClient.clearBucket(Bucket.CARTS);
        }
        EventListener<CartEvent> listener = new EventListener<>() {
            @Override
            public void onEvent(Subscription subscription, String subject, CartEvent event, long revision) {
                logger.info("New cart event gotten %s %s%n".formatted(revision, event));
                cartServiceV2.handleCartEvent(subject, event);

                // TODO: Save the checkpoint to the database
            }
        };

        SubscribeToStreamOptions options = SubscribeToStreamOptions.get().fromStart();
        // TODO: Set the fromRevision parameter if a checkpoint exists
        // options = ...

        this.eventClient.subscribe(CartService.CART_AGGREGATION_STREAM, CartEvent.class, listener, options);
    }

    @Override
    public void run(ApplicationArguments args) {
        startProductSubscription();
        startCartSubscription();
    }

}
