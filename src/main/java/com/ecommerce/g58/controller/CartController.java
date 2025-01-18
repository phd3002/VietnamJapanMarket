package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.utils.FormatVND;
import com.ecommerce.g58.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
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

    static final long MAX_CART_AMOUNT = 100000000;
    // Add to Cart
    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam("variationId") Integer variationId,
                            @RequestParam("quantity") Integer quantity,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
            return "redirect:/sign-in";
        }

        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();
        Cart cart = cartService.getOrCreateCart(user);


        try {
            ProductDetailDTO productDetail = productService.getProductDetailByProductIdAndVariationId(productId, variationId);

            // 1) Check if user tries to add their own product
            if (email.equals(productDetail.getStoreMail())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không thể thêm sản phẩm của bạn vào giỏ hàng");
                return "redirect:" + request.getHeader("Referer");
            }

            List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);
            for (CartItem item : cartItems) {
                ProductVariation variation = item.getVariationId(); // Retrieve the selected ProductVariation
                int cartQuantity = item.getQuantity();
                int availableStock = variation.getStock(); // Stock is now validated at the variation level

                if (cartQuantity > availableStock) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm \"" + variation.getProductId().getProductName() +
                            "\" (Màu: " + variation.getColor().getColorName() + ", Kích thước: " + variation.getSize().getSizeName() +
                            ") không đủ hàng trong kho. Vui lòng giảm số lượng hoặc xóa sản phẩm này khỏi giỏ hàng.");
                    redirectAttributes.addFlashAttribute("cartItems", cartItems);
                    return "redirect:" + request.getHeader("Referer");
                }
            }

            // 2) Check store mismatch
            if (!cartItems.isEmpty()) {
                Integer newProductStoreId = productDetail.getStoreId();
                Integer currentStoreId = cartItems.get(0).getProductId().getStoreId().getStoreId();
                if (!currentStoreId.equals(newProductStoreId)) {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "Bạn không thể thêm sản phẩm từ cửa hàng khác vào giỏ hàng. " +
                                    "Vui lòng thanh toán hoặc xóa các sản phẩm hiện tại trong giỏ hàng trước."
                    );
                    return "redirect:" + request.getHeader("Referer");
                }
            }

            // 3) Enforce total limit
            //    - Calculate current cart total
            int currentCartTotal = cartItems.stream()
                    .mapToInt(ci -> ci.getQuantity() * ci.getProductId().getPrice())
                    .sum();

            //    - Calculate cost of these new items
            int newItemCost = productDetail.getPrice() * quantity;

            //    - Check if it exceeds the limit
            if (currentCartTotal + newItemCost > MAX_CART_AMOUNT) {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể thêm! Tổng giỏ hàng vượt quá 100.000.000 VND.");
                return "redirect:" + request.getHeader("Referer");
            }

            // 4) If OK, add the items
            cartService.addProductToCart(user, productDetail, quantity, cart);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng của bạn");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã có lỗi xảy ra khi thêm vào giỏ hàng");
        }

        return "redirect:" + request.getHeader("Referer");
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

        // Calculate total order price as integer
        int totalOrderPrice = cartItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getProductId().getPrice())
                .sum();

        // Format the total to VND
        String formattedTotal = FormatVND.formatCurrency(BigDecimal.valueOf(totalOrderPrice));
        model.addAttribute("totalOrderPrice", formattedTotal);

        return "cart-detail"; // Thymeleaf template for cart details
    }

    // Remove Cart Item
    @PostMapping("/remove_cart_item")
    public String removeCartItem(@RequestParam("cartItemId") Integer cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart-items";
    }

    // Update Cart Quantity (non-AJAX)
    @PostMapping("/update_cart_quantity")
    public String updateCartQuantity(@RequestParam("cartItemId") Integer cartItemId,
                                     @RequestParam("quantity") Integer quantity,
                                     RedirectAttributes redirectAttributes) {
        // Fetch cart item and check product stock
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mục giỏ hàng"));
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

    // Update Cart Quantity (AJAX)
    @PostMapping("/api/cart/updateQuantity")
    @ResponseBody
    public Map<String, Object> updateCartQuantityAjax(@RequestParam("cartItemId") Integer cartItemId,
                                                      @RequestParam("quantity") Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1) Fetch the cart item
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mục giỏ hàng"));

            // 2) Check stock
            int productStock = cartItem.getVariationId().getStock();
            if (quantity > productStock) {
                response.put("success", false);
                response.put("message", "Vượt quá số lượng tồn kho");
                return response;
            }

            // 3) Compute current total,
            //    remove old subtotal (this item) from that total,
            //    then add new subtotal to see if we exceed the limit
            Integer userId = cartItem.getCart().getUser().getUserId();
            List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

            int oldSubtotal = cartItem.getQuantity() * cartItem.getProductId().getPrice();
            int currentCartTotal = cartItems.stream()
                    .mapToInt(ci -> ci.getQuantity() * ci.getProductId().getPrice())
                    .sum();

            int newSubtotalValue = quantity * cartItem.getProductId().getPrice();
            int newCartTotal = currentCartTotal - oldSubtotal + newSubtotalValue;

            // 4) Check the 100 million limit
            if (newCartTotal > MAX_CART_AMOUNT) {
                response.put("success", false);
                response.put("message", "Tổng giỏ hàng vượt quá 100.000.000 VND. Không thể cập nhật!");
                return response;
            }

            // 5) If under limit, do the actual update
            cartItemService.updateCartItemQuantity(cartItemId, quantity);

            // 6) Return success and newly updated totals
            String newSubtotalFormatted = FormatVND.formatCurrency(BigDecimal.valueOf(newSubtotalValue));
            // Recompute entire total (which should = newCartTotal anyway)
            int finalCartTotal = newCartTotal;
            String newTotalFormatted = FormatVND.formatCurrency(BigDecimal.valueOf(finalCartTotal));

            response.put("success", true);
            response.put("newSubtotal", newSubtotalFormatted);
            response.put("newTotal", newTotalFormatted);
            response.put("message", "Đã cập nhật số lượng!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi khi cập nhật số lượng");
        }
        return response;
    }

}
