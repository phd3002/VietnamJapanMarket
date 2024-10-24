package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageRepository productImageRepository; // Inject ProductImageRepository

    @GetMapping("/session-info")
    public ResponseEntity<String> getSessionInfo(HttpSession session) {
        StringBuilder sessionInfo = new StringBuilder();
        sessionInfo.append("Session ID: ").append(session.getId()).append("\n");

        LocalDateTime checkoutTime = (LocalDateTime) session.getAttribute("checkoutSession");
        if (checkoutTime != null) {
            sessionInfo.append("Checkout Session Start Time: ").append(checkoutTime).append("\n");
        } else {
            sessionInfo.append("No checkout session active.\n");
        }

        return new ResponseEntity<>(sessionInfo.toString(), HttpStatus.OK);
    }


    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/sign-in";
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();

        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            double itemPrice = item.getPrice();
            int quantity = item.getQuantity();
            totalPrice += itemPrice * quantity;
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        Map<Integer, String> productImages = new HashMap<>();
        for (CartItem item : cartItems) {
            List<ProductImage> images = productImageRepository.findByProductProductId(item.getProductId().getProductId());
            if (!images.isEmpty()) {
                productImages.put(item.getProductId().getProductId(), images.get(0).getThumbnail());
            }
        }
        model.addAttribute("productImages", productImages);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal) {
        // Log the session ID at the start of checkout
        logger.info("Session ID during checkout start: {}", session.getId());

        if (principal == null) {
            return "redirect:/sign-in";
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();

        // Log that the proceedToCheckout method was called successfully
        logger.info("Proceed to checkout called successfully for user: {}", userId);

        // Subtract item quantities from stock
        cartService.subtractItemQuantitiesFromStock(userId);

        // Create a checkout start time for 30-minute validity (instead of using session expiry)
        session.setAttribute("checkoutStartTime", LocalDateTime.now());

        return "redirect:/checkout"; // Proceed to checkout page
    }

    @PostMapping("/checkout/cancel")
    public ResponseEntity<Void> cancelCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during cancel: {}", session.getId());

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();

        // Restore item quantities to stock
        cartService.restoreItemQuantitiesToStock(userId);

        // Log session attributes to verify checkout session
        if (session.getAttribute("checkoutStartTime") != null) {
            logger.info("Checkout session found, canceling checkout for user: {}", username);
        } else {
            logger.warn("No checkout session found for user: {}", username);
        }

        // Remove the specific checkout session attribute
        session.removeAttribute("checkoutStartTime");

        return ResponseEntity.ok().build(); // Return 200 OK with no content
    }

    @GetMapping("/confirm-checkout")
    public String confirmCheckout(HttpSession session, Principal principal) {
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // Retrieve the checkout start time
        LocalDateTime checkoutStartTime = (LocalDateTime) session.getAttribute("checkoutStartTime");

        if (checkoutStartTime != null) {
            // Check if the checkout has expired (more than 30 minutes)
            if (Duration.between(checkoutStartTime, LocalDateTime.now()).toMinutes() > 30) {
                // Restore item quantities to stock if the checkout session expired
                Integer userId = userService.findByEmail(principal.getName()).getUserId();
                cartService.restoreItemQuantitiesToStock(userId);

                // Remove the checkout start time attribute
                session.removeAttribute("checkoutStartTime");

                // Redirect to cart with a message indicating that checkout has expired
                return "redirect:/cart?checkoutExpired=true";
            }
        }

        return "redirect:/payment"; // Proceed to payment page if session is valid
    }
}