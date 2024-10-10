package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Add to Cart
    @GetMapping("/add_to_cart")
    public String addToCart(@RequestParam("variationId") Integer variantId,
                            @RequestParam("productId") Integer productId,
                            @RequestParam("productName") String productName,
                            @RequestParam("imageId") Integer imageId,
                            @RequestParam("quantity") Integer quantity,
                            @RequestParam("price") Integer price,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        System.out.println("product id: " + productId);
        Products product = productService.getProductById(productId);
        System.out.println("product name: " + product.getProductName());
        ProductVariation productVariation = productService.getProductVariationById(variantId);
        System.out.println(variantId);

        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hãy đăng nhập để thêm sản phẩm vào giỏ hàng");
            return "redirect:/sign-in";
        }

        // Fetch the authenticated user's email and user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Cart cart = cartService.getOrCreateCart(user);

        model.addAttribute("user", user);

        // Get total quantity in cart
        Integer totalQuantity = cartService.getTotalQuantityByUser(user);
        session.setAttribute("totalQuantity", totalQuantity);

        System.out.println("Cart: " + cart.getCartId());
        // Add item to cart
        cartService.addToCart(cart, variantId, productId, productName, imageId, quantity, price);
        session.setAttribute("addToCartSuccess", "Sản phẩm đã được thêm vào giỏ hàng thành công");

        return "redirect:/cart-items";  // Redirect to cart items
    }

    // View Cart Items
    @GetMapping("/cart-items")
    public String getCartItems(Model model) {
        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        // Fetch the authenticated user's email and user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        // Fetch cart items by user
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Your cart is empty.");
            return "cart-detail";  // Return empty cart view
        }

        // Group cart items by store (assuming Product has a reference to Store)
        Map<Stores, List<CartItem>> groupedItems = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProductId().getStoreId()));

        model.addAttribute("cartItemGroupedByStore", groupedItems);

        // Calculate total order price
        Integer totalOrderPrice = cartItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getProductId().getPrice()).sum();
        model.addAttribute("totalOrderPrice", totalOrderPrice);

        return "cart-detail";  // Thymeleaf template for cart details
    }


    // Remove Cart Item
    @PostMapping("/remove_cart_item")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId, HttpSession session) {
        cartService.removeCartItem(cartItemId);
        session.setAttribute("cartItemRemoved", "Sản phẩm đã được xóa khỏi giỏ hàng thành công");
        return "redirect:/cart-items";
    }

    // Update Cart Item Quantity
    @PostMapping("/update_cart_quantity")
    public ResponseEntity<String> updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                                     @RequestParam("quantity") Integer quantity) {
        // Fetch cart item and check product stock
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        int productStock = cartItem.getVariationId().getStock();

        try {
            // Check if the requested quantity is within the available stock
            if (quantity <= productStock) {
                cartItemService.updateCartItemQuantity(cartItemId, quantity);
                return ResponseEntity.ok("Quantity updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity: Exceeds stock limit");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
        }
    }
//    @Autowired
//    private CartService cartService;
//    @Autowired
//    UserService userService;
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private CartItemService cartItemService;
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @GetMapping("/add_to_cart")
//    public String addToCart(@RequestParam("variantId") Integer variantId,
//                            @RequestParam("productId") Integer productId,
//                            @RequestParam("productName") String productName,
//                            @RequestParam("imageId") Integer imageId,
//                            @RequestParam("quantity") Integer quantity,
//                            @RequestParam("price") Integer price,
//                            HttpSession session,
//                            RedirectAttributes redirectAttributes,
//                            Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        // Check if the user is authenticated
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            redirectAttributes.addFlashAttribute("message", "Hãy đăng nhập để thêm sản phẩm vào giỏ hàng");
//            return "redirect:/sign-in";
//        }
//        String email = authentication.getName();
//        Users user = userService.findByEmail(email);
//        Cart cart = cartService.getOrCreateCart(user);
//        model.addAttribute("user", user);
//
//        Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
//        int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
//        session.setAttribute("totalQuantity", totalQuantity);
//        cartService.addToCart(cart, variantId, productId, productName, imageId, quantity, price);
//        session.setAttribute("addToCartSuccess", "Sản phẩm đã được thêm vào giỏ hàng thành công");
//
//        return null;
//
//    }
//
//    @GetMapping("/cart-items")
//    public String getCartItems(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/sign-in";
//        }
//        String email = authentication.getName();
//        Users user = userService.findByEmail(email);
//        Integer userId = user.getUserId();
//        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
//
//        Map<Stores, List<CartItem>> groupedItems = cartItems.stream()
//                .collect(Collectors.groupingBy(item -> item.getVariationId().getProductId().getStoreId()));
//        model.addAttribute("cartItemGroupedByStore", groupedItems);
//
//        Integer totalOrderPrice = cartItems.stream()
//                .mapToInt(item -> item.getQuantity() * item.getProductId().getPrice()).sum();
//        model.addAttribute("totalOrderPrice", totalOrderPrice);
//        return "cart-detail";
//    }
//
//    @PostMapping("/remove_cart_item")
//    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId, HttpSession session) {
//        cartService.removeCartItem(cartItemId);
//        return "redirect:/cart_items";
//    }
//
//    @PostMapping("/update_cart_quantity")
//    public ResponseEntity<String> updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
//                                                     @RequestParam("quantity") Integer quantity,
//                                                     HttpSession session) {
//        CartItem cart = cartItemRepository.getCartItemByCartItemId(cartItemId);
//        int productQuantity = cart.getVariationId().getStock();
//        try {
//            if (quantity <= productQuantity) {
//                cartItemService.updateCartItemQuantity(cartItemId, quantity);
//                return ResponseEntity.ok("Quantity updated successfully");
//            } else
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
//        }
//    }
}
