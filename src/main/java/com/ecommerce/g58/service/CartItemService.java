package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartItemService {
    // Retrieve all cart items for a specific user by their userId
    List<CartItem> getCartItemsByUserId(Integer userId);

    // Update the quantity of a cart item
    void updateCartItemQuantity(Integer cartItemId, int quantity);

    // New method to remove a single cart item by its ID
    void removeCartItemById(Integer cartItemId);

//    // Get a list of cart items by their IDs
//    List<CartItem> getCartItemsByIds(List<Integer> cartItemIds);
//
//    // Retrieve a cart item by its ID
//    CartItem getCartItemById(Integer cartItemId);
//
//    // Remove multiple cart items by their IDs
//    void removeCartItemsByIds(List<Integer> cartItemIds);
//
//    // Retrieve all cart items for a specific delivery role ID
//    List<CartItem> getCartItemsByDeliveryRoleId(Integer deliveryRoleId);


}
