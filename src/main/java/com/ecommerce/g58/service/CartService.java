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

    void addProductToCart(Users user, ProductDetailDTO productDetail, int quantity, Cart cart);

    void removeCartItem(Integer cartItemId);

    List<CartItem> getCartItemsByUserId(Integer userId);

    int getTotalQuantityByUser(Users user);

    List<CartItem> getCartItemsByIds(List<Integer> cartItemIds);

    void updateCartItemQuantity(Integer cartItemId, int quantity);

    public Cart getCartByUserId(Integer userId); // lay gio hang cua user

    int getCartItemCount(Integer userId); // lay so luong mon hang trong gio hang

    void subtractItemQuantitiesFromStock(Integer userId); // tru so luong mon hang trong zo

    void restoreItemQuantitiesToStock(Integer userId); // phuc hoi so luong mon hang trong kho

    void restoreItemQuantitiesToStock(Integer userId, Integer orderId);

    void clearCart(Integer userId);
}
