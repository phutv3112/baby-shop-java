package com.phs.application.repository;

import com.phs.application.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Cart findByUserIdAndProductId(Long userId, String productId);
}
