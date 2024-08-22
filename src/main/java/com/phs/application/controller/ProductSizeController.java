package com.phs.application.controller;

import com.phs.application.model.dto.Product_quantity;
import com.phs.application.service.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-size")
public class ProductSizeController {

    @Autowired
    private ProductSizeService productSizeService;

    @GetMapping
    public ResponseEntity<List<Product_quantity>> getProductSizes(@RequestParam String productId) {
        List<Product_quantity> product_quantities = productSizeService.getProductSizes(productId);
        return ResponseEntity.ok(product_quantities);
    }
}