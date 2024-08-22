package com.phs.application.model;

import java.util.List;

public class ProductResponse1 {
    private List<ProductDTO1> products;
    private String status;
    private String message;

    // Constructor, getters, and setters
    public ProductResponse1(List<ProductDTO1> products, String status, String message) {
        this.products = products;
        this.status = status;
        this.message = message;
    }

    // Getters and setters
}
