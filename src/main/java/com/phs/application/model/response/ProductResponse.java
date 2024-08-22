package com.phs.application.model.response;

import com.phs.application.model.dto.ProductInfoDTO;

import java.util.List;

public class ProductResponse {
    private List<ProductInfoDTO> products;
    private String status;
    private String message;

    public ProductResponse(List<ProductInfoDTO> products, String status, String message) {
        this.products = products;
        this.status = status;
        this.message = message;
    }

    public List<ProductInfoDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInfoDTO> products) {
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
