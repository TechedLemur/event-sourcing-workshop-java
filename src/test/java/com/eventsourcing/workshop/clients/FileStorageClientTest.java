package com.eventsourcing.workshop.clients;

import com.eventsourcing.workshop.models.Bucket;
import com.eventsourcing.workshop.models.Cart;
import com.eventsourcing.workshop.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageClientTest {

    @Test
    void roundTripsCartProductAndCheckpoint(@TempDir Path dir) {
        Path file = dir.resolve("store.json");
        ObjectMapper mapper = new ObjectMapper();
        FileStorageClient client = new FileStorageClient(mapper, file.toString());

        Cart cart = new Cart("c1");
        client.put(Bucket.CARTS, "c1", cart);
        assertTrue(client.get(Bucket.CARTS, "c1").isPresent());
        assertEquals("c1", client.get(Bucket.CARTS, "c1").map(Cart.class::cast).orElseThrow().getId());

        Product p = new Product("p1");
        client.put(Bucket.PRODUCTS, "p1", p);
        assertEquals("p1", client.get(Bucket.PRODUCTS, "p1").map(Product.class::cast).orElseThrow().getId());

        client.put(Bucket.CHECKPOINTS, "products", 42L);
        assertEquals(42L, client.get(Bucket.CHECKPOINTS, "products").orElseThrow());

        // Reload from disk
        FileStorageClient client2 = new FileStorageClient(mapper, file.toString());
        assertEquals(42L, client2.get(Bucket.CHECKPOINTS, "products").orElseThrow());
        assertEquals("c1", client2.get(Bucket.CARTS, "c1").map(Cart.class::cast).orElseThrow().getId());
    }
}
