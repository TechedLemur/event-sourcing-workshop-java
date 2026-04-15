package com.eventsourcing.workshop.controllers;

import com.eventsourcing.workshop.services.ProductServiceV2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({ "/products/v2" })
public class ProductControllerV2 extends ProductController {

    public ProductControllerV2(ProductServiceV2 productService) {
        super(productService);
    }
}
