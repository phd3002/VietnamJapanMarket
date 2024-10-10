package com.ecommerce.g58.service.implementation;

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
    public void addToCart(Cart cart, Integer variationId, Integer productId, String productName, Integer imageId, int quantity, Integer price) {
        if (cart.getCartItems() == null) {
            cart.setCartItems(new ArrayList<>());  // Khởi tạo danh sách trống nếu chưa có
        }
        // Log to check parameters
        System.out.println("Adding to cart - productId: " + productId + ", variantId: " + variationId);
        // Fetch the product and variation
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductVariation variation = productVariationRepository.findById(variationId)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found"));

        // Find the cart item by cart and variation
        CartItem cartItem = cartItemRepository.findByCartIdAndVariation(cart.getCartId(), variation);

        if (cartItem == null) {
            System.out.println("Creating new cart item");
            // If the item doesn't exist in the cart, create a new CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(product);
            cartItem.setVariationId(variation);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(price);
            cartItemRepository.save(cartItem);
        } else {
            System.out.println("Updating cart item quantity");
            // If the item already exists, just update the quantity
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        }

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




//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private CartRepository cartRepository;
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//
//    @org.springframework.transaction.annotation.Transactional
//    @Override
//    public Integer getTotalQuantityByUser(Users user) {
//        return cartRepository.getTotalQuantityByUser(user);
//
//    }
//
//    @Override
//    @Transactional
//    public Cart getOrCreateCart(Users user) {
//        Optional<Cart> optionalCart = cartRepository.findByUser(user);
//        if (optionalCart.isPresent()) {
//            return optionalCart.get();
//        }
//
//        Cart cart = Cart.builder()
//                .cartId(0)
//                .user(user)
//                .totalPrice(0)
//                .build();
//        return cartRepository.save(cart);
//    }
//
//
//    @Override
//    @Transactional
//    public void addToCart(Cart cart, Integer variantId, Integer productId, String productName, Integer imageId, Integer quantity, Integer price) {
//        Products product = productRepository.findById(productId)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));
//        // Lay danh sach cac san pham trong gio hang
//        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//        Optional<CartItem> optionalCartItem = cartItemRepository.findByCartAndProduct(cart, product);
//
//        if (optionalCartItem.isPresent()) {
//            CartItem cartItem = optionalCartItem.get();
//            cartItem.setQuantity(cartItem.getQuantity() + quantity);
//            cartItemRepository.save(cartItem);
//        } else {
//            CartItem cartItem = CartItem.builder()
//                    .cart(cart)
//                    .productId(product)
//                    .quantity(quantity)
//                    .price(price)
//                    .build();
//            cartItemRepository.save(cartItem);
//        }
//    }
//
//    @org.springframework.transaction.annotation.Transactional
//    @Override
//    public void removeCartItem(Integer cartItemId) {
//        cartItemRepository.deleteById(cartItemId);
//    }
//
//    @Override
//    public List<Cart> getListCartProvisionalByDeliveryRoleId(Integer deliveryRoleId) {
//        return cartRepository.findListCartsByDeliveryRoleIdAndStatusProvisional(deliveryRoleId);
//
//    }
//    @Override
//    public Cart getCartProvisionalByDeliveryRoleId(Integer deliveryRoleId) {
//        return cartRepository.findCartsByDeliveryRoleIdAndStatusProvisional(deliveryRoleId);
//
//    }
}
