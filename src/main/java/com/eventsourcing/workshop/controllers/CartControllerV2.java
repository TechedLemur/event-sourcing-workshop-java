package com.eventsourcing.workshop.controllers;

import com.eventsourcing.workshop.services.CartServiceV2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({ "/cart/v2" })
public class CartControllerV2 extends CartController {

    public CartControllerV2(CartServiceV2 cartService) {
        super(cartService);
    }

}
