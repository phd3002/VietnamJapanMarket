package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface CartService {
    Cart getOrCreateCart(Users user);

    Integer getTotalQuantityByUser(Users user);

    void addToCart(Cart cart, Integer variantId, Integer productId, String productName, Integer imageId, Integer quantity, BigDecimal price);
}
