package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private SecurityUtils securityUtils;


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
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
            return "redirect:/sign-in";
        }

        // Fetch user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();
        Cart cart = cartService.getOrCreateCart(user);

        try {
            // Fetch product variation details (using the variationId)
            ProductDetailDTO productDetail = productService.getProductDetailByProductIdAndVariationId(productId, variationId);
//            System.out.println("Product detail Store: " + productDetail.getStoreId());

            if (productDetail != null) {
                List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
                if (!cartItems.isEmpty()) {
                    Integer newProductStoreId = productDetail.getStoreId();// Get storeId from productDetail
//                    System.out.println("New product store ID: " + newProductStoreId);
                    Integer currentStoreId = cartItems.get(0).getProductId().getStoreId().getStoreId();


                // Check if the stores are different
                    if (!currentStoreId.equals(newProductStoreId)) {
//                        System.out.println("Current store ID: " + currentStoreId);
//                        System.out.println("New product store ID: " + newProductStoreId);
                        redirectAttributes.addFlashAttribute("error", "Bạn không thể thêm sản phẩm từ cửa hàng khác vào giỏ hàng. Vui lòng thanh toán hoặc xóa các sản phẩm hiện tại trong giỏ hàng trước.");
                        String referer = request.getHeader("Referer");
                        return "redirect:" + referer;  // Redirects to the same page
                    }
                }

                // Add the product to the cart
                cartService.addProductToCart(user, productDetail, quantity, cart);

                // Success message
                redirectAttributes.addFlashAttribute("message", "Sản phâ đã được thêm vào giỏ hàng của bạn");
            } else {
                // Error message if the product detail is not found
                redirectAttributes.addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
            }
        } catch (Exception e) {
            // Handle exceptions and add an error message
            redirectAttributes.addFlashAttribute("error", "Đã có lỗi xảy ra khi thêm vào giỏ hảng");
        }

        // Redirect to the same product-detail page (stay on the same page)
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }


    // View Cart Items
    @GetMapping("/cart-items")
    public String getCartItems(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
        boolean isCartEmpty = cartItems.isEmpty();

        model.addAttribute("isCartEmpty", isCartEmpty);
        model.addAttribute("cartItems", cartItems);

        // Group cart items by store if the cart is not empty
        Map<Stores, List<CartItem>> groupedItems = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProductId().getStoreId()));
        model.addAttribute("cartItemGroupedByStore", groupedItems);

        // Calculate total order price
        Integer totalOrderPrice = cartItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getProductId().getPrice())
                .sum();
        model.addAttribute("totalOrderPrice", totalOrderPrice);

        return "cart-detail"; // Thymeleaf template for cart details
    }


    // Remove Cart Item
    @PostMapping("/remove_cart_item")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart-items";
    }

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
                redirectAttributes.addFlashAttribute("message", "Cập nhật giỏ hàng thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật số lượng: Vượt quá số lượng tồn kho");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi cập nhật số lượng");
        }

        // Redirect back to cart detail page
        return "redirect:/cart-items";
    }

    @GetMapping("/api/cart/count")
    @ResponseBody
    public int getCartItemCount() {
        Integer userId = securityUtils.getCurrentUserId();
        if (userId != null) {
            return cartService.getCartItemCount(userId);
        }
        return 0;
    }
}
