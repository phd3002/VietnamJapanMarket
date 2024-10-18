package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.Users;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface CartService {
    Cart getOrCreateCart(Users user);

//    void addToCart(Cart cart, Integer variantId, Integer productId, String productName, Integer imageId, int quantity, Integer price);

    void addProductToCart(Users user, ProductDetailDTO productDetail, int quantity, Cart cart);

    void removeCartItem(Integer cartItemId);

    List<CartItem> getCartItemsByUserId(Integer userId);

    int getTotalQuantityByUser(Users user);

    List<CartItem> getCartItemsByIds(List<Integer> cartItemIds);

    void updateCartItemQuantity(Integer cartItemId, int quantity);
    //    Cart getOrCreateCart(Users user);
//
//    Integer getTotalQuantityByUser(Users user);
//
//    void addToCart(Cart cart, Integer variantId, Integer productId, String productName, Integer imageId, Integer quantity, Integer price);
//
//    void removeCartItem(Integer cartItemId);
//
//    List<Cart> getListCartProvisionalByDeliveryRoleId(Integer deliveryRoleId);
//
//    Cart getCartProvisionalByDeliveryRoleId(Integer deliveryRoleId);
// Retrieve or create a cart for the user
}
