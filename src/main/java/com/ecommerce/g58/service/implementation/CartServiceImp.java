package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.repository.CartRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImp implements CartService {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;


    @org.springframework.transaction.annotation.Transactional
    @Override
    public Integer getTotalQuantityByUser(Users user) {
        return cartRepository.getTotalQuantityByUser(user);

    }

    @Override
    @Transactional
    public Cart getOrCreateCart(Users user) {
        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        if (optionalCart.isPresent()) {
            return optionalCart.get();
        }

        Cart cart = Cart.builder()
                .cartId(0)
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }


    @Override
    @Transactional
    public void addToCart(Cart cart, Integer variantId, Integer productId, String productName, Integer imageId, Integer quantity, BigDecimal price) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
        // Lay danh sach cac san pham trong gio hang
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .productId(product)
                    .quantity(quantity)
                    .price(price)
                    .build();
            cartItemRepository.save(cartItem);
        }
    }
}
