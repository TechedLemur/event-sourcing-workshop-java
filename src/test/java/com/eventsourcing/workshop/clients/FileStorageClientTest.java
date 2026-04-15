package com.eventsourcing.workshop.clients;

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
        client.put("carts", "c1", cart);
        assertTrue(client.get("carts", "c1").isPresent());
        assertEquals("c1", client.get("carts", "c1").map(Cart.class::cast).orElseThrow().getId());

        Product p = new Product("p1");
        client.put("products", "p1", p);
        assertEquals("p1", client.get("products", "p1").map(Product.class::cast).orElseThrow().getId());

        client.put("checkpoints", "products", 42L);
        assertEquals(42L, client.get("checkpoints", "products").orElseThrow());

        // Reload from disk
        FileStorageClient client2 = new FileStorageClient(mapper, file.toString());
        assertEquals(42L, client2.get("checkpoints", "products").orElseThrow());
        assertEquals("c1", client2.get("carts", "c1").map(Cart.class::cast).orElseThrow().getId());
    }
}
