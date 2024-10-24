package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.repository.CartRepository;
import com.ecommerce.g58.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class CartItemServiceImp implements CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getCartItemsByUserId(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public void updateCartItemQuantity(Integer cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeCartItemById(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

}
