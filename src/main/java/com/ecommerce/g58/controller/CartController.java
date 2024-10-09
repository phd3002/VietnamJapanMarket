package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
public class CartController {
    @Autowired
    private  CartService cartService;
    @Autowired
    UserService userService;
    @Autowired
    private ProductService productService;

    @GetMapping("/add_to_cart")
    public String addToCart(@RequestParam("variantId") Integer variantId,
                            @RequestParam("productId") Integer productId,
                            @RequestParam("productName") String productName,
                            @RequestParam("imageId") Integer imageId,
                            @RequestParam("quantity") Integer quantity,
                            @RequestParam("price") BigDecimal price,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("message", "Please login to add to cart!");
            return "redirect:/login";
        }
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("user", user);

        Integer finalTotalQuantity = cartService.getTotalQuantityByUser(user);
        int totalQuantity = finalTotalQuantity != null ? finalTotalQuantity : 0;
        session.setAttribute("totalQuantity", totalQuantity);
        cartService.addToCart(cart, variantId, productId, productName, imageId, quantity, price);

        return null;

    }
}
