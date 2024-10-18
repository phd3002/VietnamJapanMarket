package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.repository.CartRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImp implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Override
    public Cart getOrCreateCart(Users user) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalAmount(0);
            cart.setTotalPrice(0);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public void addProductToCart(Users user, ProductDetailDTO productDetail, int quantity, Cart cart) {
        // Fetch the product and variation as entities from the database
        Products product = productRepository.findById(productDetail.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        ProductVariation variation = productVariationRepository.findById(productDetail.getVariationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid variation ID"));

        // Check if the product variation is already in the cart
        CartItem cartItem = cartItemRepository.findByCartAndProductAndVariation(cart, product, variation);

        if (cartItem != null) {
            // If the item is already in the cart, update the quantity
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // If the item is not in the cart, create a new CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(product);  // Use entity Products
            cartItem.setVariationId(variation);  // Use entity ProductVariation
            cartItem.setQuantity(quantity);
            cartItem.setPrice(productDetail.getPrice());  // Store the price from the ProductDetailDTO
            cartItemRepository.save(cartItem);
        }
        cartRepository.save(cart);
        updateCartTotalPrice(cart);
    }

    @Transactional
    @Override
    public void removeCartItem(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public List<CartItem> getCartItemsByUserId(Integer userId) {
        Cart cart = cartRepository.findByUser_UserId(userId);
        return cartItemRepository.findByCart_CartId(cart.getCartId());
    }

    @Override
    public int getTotalQuantityByUser(Users user) {
        Cart cart = cartRepository.findByUser(user);
        return cart != null ? cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }

    @Override
    public void updateCartItemQuantity(Integer cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        // Update the total price of the cart
        updateCartTotalPrice(cartItem.getCart());
    }

    @Override
    public List<CartItem> getCartItemsByIds(List<Integer> cartItemIds) {
        return cartItemRepository.findByCartItemIdIn(cartItemIds);
    }

    public void updateCartTotalPrice(Cart cart) {
        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            int totalPrice = cart.getCartItems().stream()
                    .mapToInt(item -> item.getQuantity() * item.getPrice())
                    .sum();
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
        } else {
            cart.setTotalPrice(0);// Đặt giá trị mặc định nếu giỏ hàng trống
            cartRepository.save(cart);
        }
    }

    @Override
    public Cart getCartByUserId(Integer userId) {
        return cartRepository.findByUser_UserId(userId);
    }
}
