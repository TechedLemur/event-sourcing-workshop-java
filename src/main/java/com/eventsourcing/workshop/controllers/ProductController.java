package com.eventsourcing.workshop.controllers;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import com.eventsourcing.workshop.services.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventsourcing.workshop.models.Product;

@RestController
@RequestMapping({ "/products/v1", "/products" })
public class ProductController {
    private static final Logger logger = Logger.getLogger(ProductController.class.getName());
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Collection<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        logger.info("Getting product by ID: " + id);
        Optional<Product> product = productService.getProduct(id);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product.get());
    }

    public record UpdateProductPriceRequest(double price, String currency) {
    }

    @PatchMapping("/{id}/price")
    public void updateProductPrice(@PathVariable String id, @RequestBody UpdateProductPriceRequest request) {
        logger.info("Updating product price: " + id + " to " + request.price());
        productService.updateProductPrice(id, request.price(), request.currency());
    }

}
