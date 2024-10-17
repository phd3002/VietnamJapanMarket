package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private ProductVariationRepository productVariationRepository;
    @Autowired
    private ProductRepository productRepository;

    // Add to Cart
    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("variationId") Integer variationId,
                            @RequestParam("quantity") Integer quantity,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        // Fetch user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Cart cart = cartService.getOrCreateCart(user);

        try {
            // Fetch product variation details (using the variationId)
            ProductDetailDTO productDetail = productService.getProductDetailByProductIdAndVariationId(productId, variationId);

            if (productDetail != null) {
                // Add the product to the cart
                cartService.addProductToCart(user, productDetail, quantity,cart);

                // Success message
                redirectAttributes.addFlashAttribute("message", "Product successfully added to your cart!");
            } else {
                // Error message if the product detail is not found
                redirectAttributes.addFlashAttribute("error", "Failed to add product to cart. Product not found.");
            }
        } catch (Exception e) {
            // Handle exceptions and add an error message
            redirectAttributes.addFlashAttribute("error", "Error adding product to cart. Please try again.");
        }

        // Redirect to the same product-detail page (stay on the same page)
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }


//    @GetMapping("/add_to_cart")
//    public String addToCart(@RequestParam("productId") Integer productId,
//                            @RequestParam("variationId") Integer variationId,
//                            @RequestParam("quantity") Integer quantity,
//                            @AuthenticationPrincipal Users user,
//                            RedirectAttributes redirectAttributes) {
//
//        // Fetch the product details (you could use a service method to get ProductDetailDTO here)
//        ProductDetailDTO productDetail = productService.getProductDetailByProductIdAndVariationId(productId, variationId);
//
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Hãy đăng nhập để thêm sản phẩm vào giỏ hàng");
//            return "redirect:/sign-in";
//        }
//        // Check if productDetail exists
//        if (productDetail == null) {
//            redirectAttributes.addFlashAttribute("error", "Product not found.");
//            return "cart-detail";
//        }
//
//        String email = authentication.getName();
//        user = userService.findByEmail(email);
//        Cart cart = cartService.getOrCreateCart(user);
//
//        // Add the product to the user's cart
//        cartService.addProductToCart(user, productDetail, quantity, cart);
//
//        // Optionally, add a success message
//        redirectAttributes.addFlashAttribute("message", "Product added to cart successfully.");
//
//        // Redirect back to the product detail page or cart page
//        return "cart-detail";  // Redirect to cart page or wherever you'd like
//    }
//    @GetMapping("/add_to_cart")
//    public String addToCart(@RequestParam("variationId") Integer variantId,
//                            @RequestParam("productId") Integer productId,
//                            @RequestParam("productName") String productName,
//                            @RequestParam("imageId") Integer imageId,
//                            @RequestParam("quantity") Integer quantity,
//                            @RequestParam("price") Integer price,
//                            HttpSession session,
//                            RedirectAttributes redirectAttributes,
//                            Model model) {
//        System.out.println("product id: " + productId);
//        Products product = productService.getProductById(productId);
//        System.out.println("product name: " + product.getProductName());
//        ProductVariation productVariation = productService.getProductVariationById(variantId);
//        System.out.println(variantId);
//
//        // Get authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Hãy đăng nhập để thêm sản phẩm vào giỏ hàng");
//            return "redirect:/sign-in";
//        }
//
//        // Fetch the authenticated user's email and user data
//        String email = authentication.getName();
//        Users user = userService.findByEmail(email);
//        Cart cart = cartService.getOrCreateCart(user);
//
//        model.addAttribute("user", user);
//
//        // Get total quantity in cart
//        Integer totalQuantity = cartService.getTotalQuantityByUser(user);
//        session.setAttribute("totalQuantity", totalQuantity);
//
//        System.out.println("Cart: " + cart.getCartId());
//        // Add item to cart
//        cartService.addToCart(cart, variantId, productId, productName, imageId, quantity, price);
//        session.setAttribute("addToCartSuccess", "Sản phẩm đã được thêm vào giỏ hàng thành công");
//
//        return "redirect:/cart-items";  // Redirect to cart items
//    }

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
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
//        session.setAttribute("cartItemRemoved", "Sản phẩm đã được xóa khỏi giỏ hàng thành công");
        return "redirect:/cart-items";
    }

    // Update Cart Item Quantity
//    @PostMapping("/update_cart_quantity")
//    public ResponseEntity<String> updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
//                                                     @RequestParam("quantity") Integer quantity) {
//        // Fetch cart item and check product stock
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
//        int productStock = cartItem.getVariationId().getStock();
//
//        try {
//            // Check if the requested quantity is within the available stock
//            if (quantity <= productStock) {
//                cartItemService.updateCartItemQuantity(cartItemId, quantity);
//                return ResponseEntity.ok("Quantity updated successfully");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity: Exceeds stock limit");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating quantity");
//        }
//    }
    @PostMapping("/update_cart_quantity")
    public String updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                     @RequestParam("quantity") Integer quantity,
                                     RedirectAttributes redirectAttributes) {
        // Fetch cart item and check product stock
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        int productStock = cartItem.getVariationId().getStock();

        try {
            // Check if the requested quantity is within the available stock
            if (quantity <= productStock) {
                cartItemService.updateCartItemQuantity(cartItemId, quantity);
                redirectAttributes.addFlashAttribute("message", "Quantity updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error updating quantity: Exceeds stock limit");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating quantity");
        }

        // Redirect back to cart detail page
        return "redirect:/cart-items";
    }

}
