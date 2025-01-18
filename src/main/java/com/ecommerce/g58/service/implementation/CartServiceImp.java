package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartServiceImp implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImp.class);

    @Autowired
    private CartRepository cartRepository;

   @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

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
    public List<CartItem> getCartItemsByIds(List<Integer> cartItemIds) {
        return List.of();
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

    @Override
    public int getCartItemCount(Integer userId) {
        return cartItemRepository.countCartItemsByUserId(userId);
    }

    @Override
    @Transactional
    public void subtractItemQuantitiesFromStock(Integer userId) {
        logger.info("Called subtractItemQuantitiesFromStock for user ID: {}", userId);
        List<CartItem> cartItems = getCartItemsByUserId(userId); // lấy các mặt hàng trong giỏ hàng theo ID người dùng
        // Lặp qua các mặt hàng trong giỏ hàng và kiểm tra số lượng hàng tồn kho
        for (CartItem item : cartItems) {
            ProductVariation variation = productVariationRepository.findById(item.getVariationId().getVariationId())
                    .orElseThrow(() -> new EntityNotFoundException("Product variation not found"));
            // Kiểm tra xem có đủ hàng tồn kho không
            if (variation.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock available for product: " + variation.getVariationId());
            }
            // Trừ số lượng hàng
            System.out.println("Stock before: " + variation.getStock());
            System.out.println("Quantity to subtract: " + item.getQuantity());
            variation.setStock(variation.getStock() - item.getQuantity());
            // Lưu thay đổi
            productVariationRepository.save(variation);
        }
        // log dữ liệu
        logger.info("Successfully subtracted item quantities for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void restoreItemQuantitiesToStock(Integer userId) {
        logger.info("Called restoreItemQuantitiesToStock for user ID: {}", userId);
        List<CartItem> cartItems = getCartItemsByUserId(userId); // lấy các mặt hàng trong giỏ hàng theo ID người dùng
        for (CartItem item : cartItems) {
            ProductVariation variation = productVariationRepository.findById(item.getVariationId().getVariationId())
                    .orElseThrow(() -> new EntityNotFoundException("Product variation not found"));
            // Tăng số lượng hàng trở lại
            variation.setStock(variation.getStock() + item.getQuantity());
            productVariationRepository.save(variation);
        }
        logger.info("Successfully restored item quantities for user ID: {}", userId);
    }

    @Override
    public void restoreItemQuantitiesToStock(Integer userId, Integer orderId) {
        // Find the order by ID
        Orders orders = orderRepository.findOrdersByOrderId(orderId);

        if (orders == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }

        // Retrieve order details for the given order
        List<OrderDetails> details = orderDetailRepository.findByOrderId(orderId);

        if (details.isEmpty()) {
            throw new IllegalArgumentException("No order details found for Order ID " + orderId);
        }

        for (OrderDetails detail : details) {
            ProductVariation variation = detail.getVariationId(); // Get the product variation
            int quantityToRestore = detail.getQuantity(); // Get the quantity from the order details

            if (variation != null) {
                // Update the stock of the product variation
                int updatedStock = variation.getStock() + quantityToRestore;
                variation.setStock(updatedStock);
                productVariationRepository.save(variation); // Save the updated variation
            } else {
                throw new IllegalArgumentException("Product variation not found for order detail with ID " + detail.getOrderDetailId());
            }
        }

        // Optionally: Log the restoration
        logger.info("Restored item quantities to stock for Order ID {} by User ID {}", orderId, userId);
    }


    @Override
    public void clearCart(Integer userId) {
        Cart cart = getCartByUserId(userId);
        cart.getCartItems().clear(); // Or use a repository method to delete all items.
        cartRepository.save(cart); // Ensure changes are saved.
    }
}