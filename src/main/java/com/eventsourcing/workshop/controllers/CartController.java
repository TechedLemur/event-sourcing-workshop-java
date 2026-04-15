package com.eventsourcing.workshop.controllers;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventsourcing.workshop.models.Cart;
import com.eventsourcing.workshop.services.CartService;

@RestController
@RequestMapping({ "/cart/v1", "/cart" })
public class CartController {

    private final CartService cartService;

    private static final Logger logger = Logger.getLogger(CartController.class.getName());

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{id}")
    public Cart getCart(@PathVariable String id) {
        logger.info("Getting cart by ID: " + id);
        return cartService.getCart(id).orElse(new Cart(id));
    }

    @GetMapping("/positioned/{id}")
    public Cart getCartPositioned(@PathVariable String id, @RequestParam(defaultValue = "10") long maxCount) {
        logger.info("Getting cart by ID: " + id);
        return cartService.getCart(id, maxCount).orElse(new Cart(id));
    }

    public record GetCartLastEventRevisionResponse(Long lastEventRevision) {
    }

    @GetMapping("/{id}/lastEventRevision")
    public GetCartLastEventRevisionResponse getCartLastEventRevision(@PathVariable String id) {
        logger.info("Getting cart last event revision by ID: " + id);
        return new GetCartLastEventRevisionResponse(cartService.getCartLastEventRevision(id));
    }

    public record AddItemToCartRequest(String productId) {
    }

    @PostMapping("/{cartId}/addItem")
    public void addItemToCart(@PathVariable String cartId, @RequestBody AddItemToCartRequest request) {
        logger.info("Adding item to cart: " + cartId + " with product ID: " + request.productId());
        cartService.addItemToCart(cartId, request.productId());
    }

    @DeleteMapping("/{cartId}/removeItem/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String cartId, @PathVariable UUID itemId) {
        logger.info("Removing item from cart: " + cartId + " with item ID: " + itemId);
        cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.noContent().build();
    }
}
