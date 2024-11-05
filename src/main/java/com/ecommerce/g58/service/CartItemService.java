package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.CartItem;

import java.util.List;

public interface CartItemService {
    // Retrieve all cart items for a specific user by their userId
    List<CartItem> getCartItemsByUserId(Integer userId);

    // Update the quantity of a cart item
    void updateCartItemQuantity(Integer cartItemId, int quantity);

    // New method to remove a single cart item by its ID
    void removeCartItemById(Integer cartItemId);

}
