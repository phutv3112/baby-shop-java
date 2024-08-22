package com.phs.application.service;

import com.phs.application.entity.ProductSize;
import com.phs.application.model.dto.Product_quantity;
import com.phs.application.repository.ProductSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductSizeService {

    @Autowired
    private ProductSizeRepository productSizeRepository;

    public List<Product_quantity> getProductSizes(String productId) {
        List<ProductSize> productSizes = productSizeRepository.findByProductId(productId);
        return productSizes.stream()
                .map(ps -> new Product_quantity(ps.getProductId(), ps.getQuantity()))
                .collect(Collectors.toList());
    }
}

