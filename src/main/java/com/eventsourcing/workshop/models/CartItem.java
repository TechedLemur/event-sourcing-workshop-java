package com.eventsourcing.workshop.models;

import java.util.UUID;

public record CartItem(UUID id, String productId, String productName, double productPrice,
                String productCurrency) {
}
