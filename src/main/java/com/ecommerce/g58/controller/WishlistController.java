package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wishlist;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WishlistController {
    @Autowired
    private UserService userService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ProductService productService;

    @GetMapping("/wishlist")
    public String getWishlist(Model model, RedirectAttributes redirectAttributes) {
        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào danh sách mong muốn");
            return "redirect:/sign-in";
        }

        // Fetch the authenticated user's email and user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        // Fetch wishlist items by user
        List<Wishlist> wishlistItems = wishlistService.getUserWishlist(userId);

        if (wishlistItems.isEmpty()) {
            model.addAttribute("message", "Your wishlist is empty.");
            return "wishlist";  // Return empty wishlist view
        }

        model.addAttribute("wishlistItems", wishlistItems);
        return "wishlist";  // Thymeleaf template for wishlist details
    }

    @PostMapping("/add_to_wishlist")
    public String addToWishlist(@RequestParam("productId") Integer productId,
                                @RequestParam("variationId") Integer variationId,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào mục ưa thích");
            return "redirect:/sign-in";
        }

        // Fetch user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);

        try {
            // Fetch product variation details
            ProductDetailDTO productDetail = new ProductDetailDTO();
            productDetail.setProductId(productId);
            productDetail.setVariationId(variationId);

            // Add the product to the wishlist
            wishlistService.addProductToWishlist(user, productDetail);

            // Success message
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào mục ưa thích của bạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm đã tồn tại trong mục ưa thích của bạn!");
        }

        // Redirect to the same product-detail page (stay on the same page)
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }


    @PostMapping("/remove_wishlist")
    public String removeFromWishlist(@RequestParam("wishlistId") Integer wishlistId,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {

        // Get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        try {
            // Remove the product from the wishlist
            wishlistService.removeProductFromWishlist(wishlistId);

            // Success message
            redirectAttributes.addFlashAttribute("message", "Product successfully removed from your wishlist!");
        } catch (Exception e) {
            // Handle exceptions and add an error message
            redirectAttributes.addFlashAttribute("error", "Error removing product from wishlist. Please try again.");
        }

        // Redirect to the wishlist page
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }

}
