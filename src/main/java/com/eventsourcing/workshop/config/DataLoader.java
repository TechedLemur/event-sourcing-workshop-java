package com.eventsourcing.workshop.config;

import com.eventsourcing.workshop.models.Product;
import com.eventsourcing.workshop.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
        private final ProductService productService;

        Logger logger = LoggerFactory.getLogger(DataLoader.class);

        public DataLoader(ProductService productService) {
                this.productService = productService;
        }

        private static final ArrayList<Product> SEED_PRODUCTS = new ArrayList<>(List.of(
                        new Product(
                                        "1",
                                        "Wireless Headphones",
                                        99.99,
                                        "NOK",
                                        "High-quality wireless headphones with noise cancellation",
                                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop"),
                        new Product(
                                        "2",
                                        "Smart Watch",
                                        249.99,
                                        "NOK",
                                        "Feature-rich smartwatch with fitness tracking",
                                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop"),
                        new Product(
                                        "3",
                                        "Laptop Stand",
                                        49.99,
                                        "NOK",
                                        " Ergonomic aluminum laptop stand for better posture",
                                        "https://images.unsplash.com/photo-1629317480872-45e07211ffd4?w=400&h=400&fit=crop"),
                        new Product(
                                        "4",
                                        "Mechanical Keyboard",
                                        129.99,
                                        "NOK",
                                        "RGB mechanical keyboard with cherry MX switches",
                                        "https://images.unsplash.com/photo-1626958390943-a70309376444??w=400&h=400&fit=crop"),
                        new Product(
                                        "5",
                                        "USB-C Hub",
                                        39.99,
                                        "NOK",
                                        "Multi-port USB-C hub with HDMI and SD card reader",
                                        "https://images.unsplash.com/photo-1616578781650-cd818fa41e57?w=400&h=400&fit=crop"),
                        new Product(
                                        "6",
                                        "Wireless Mouse",
                                        29.99,
                                        "NOK",
                                        "Ergonomic wireless mouse with long battery life",
                                        "https://images.unsplash.com/photo-1527814050087-3793815479db?w=400&h=400&fit=crop")));

        private void seedProduct(Product product) {
                this.logger.info("Seeding product {} - {}", product.getId(), product.getName());
                this.productService.createProduct(product);
        }

        @Override
        public void run(String... args) throws Exception {

                if (this.productService.getAllProducts().size() == SEED_PRODUCTS.size()) {
                        this.logger.info("Products already seeded, skipping");
                        return;
                }

                this.logger.info("Seeding products");
                for (Product product : SEED_PRODUCTS) {
                        this.seedProduct(product);
                }
                this.logger.info("Finished seeding products");
        }
}
