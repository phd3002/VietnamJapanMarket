package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Override
//    public List<CartItem> getCartItemsByUserId(Integer userId) {
//        return cartItemRepository.findCartItemsByUserId(userId);
//    }
//
//    @Transactional
//    public void updateCartItemQuantity(Integer cartItemId, int quantity) {
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
//        Cart cart = cartRepository.findById(cartItem.getCart().getCartId())
//                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
//        cartItem.setQuantity(quantity);
//        cart.setTotalPrice(cart.getCartItems().stream()
//                .mapToInt(item -> item.getQuantity() * item.getPrice())
//                .sum());
//        cartItemRepository.save(cartItem);
//    }
//
//    @Transactional
//    @Override
//    public void removeCartItemsByIds(List<Integer> cartItemIds) {
//        cartItemRepository.deleteByIds(cartItemIds);
//    }
//
//    @Override
//    public List<CartItem> getCartItemsByIds(List<Integer> cartItemIds) {
//        return cartItemRepository.findByCartItemIdIn(cartItemIds);
//    }
//
//    @Override
//    public CartItem getCartItemById(Integer cartItemId) {
//        return cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with id: " + cartItemId));
//    }
//
//
//
//    @Override
//    public List<CartItem> getCartItemsByDeliveryRoleId(Integer deliveryRoleId) {
//        return cartItemRepository.findCartItemsByDeliveryRoleId(deliveryRoleId);
//    }
//
//    @Transactional
//    public void removeCartItemById(Integer cartItemId) {
//        cartItemRepository.deleteById(cartItemId);
//    }
}
