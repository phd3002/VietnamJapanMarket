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
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private static final int SESSION_TIMEOUT_MINUTES = 30; // Set to 1 for testing, change to 30 for production

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageRepository productImageRepository;

    // Display the checkout page
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal, HttpSession session) {
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");

        if (sessionStartTime == null) {
            sessionStartTime = LocalDateTime.now();
            session.setAttribute("sessionStartTime", sessionStartTime);
        }

        Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
        logger.info("Session duration for ID {}: {} minutes", session.getId(), duration.toMinutes());

        if (duration.toMinutes() > SESSION_TIMEOUT_MINUTES) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                logger.info("Restoring item quantities for user ID: {}", userId);
                cartService.restoreItemQuantitiesToStock(userId);
            }
            session.invalidate();
            logger.info("Session expired and invalidated. Redirecting to homepage.");
            return "redirect:/";
        }

        if (principal == null) {
            return "redirect:/sign-in";
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        session.setAttribute("userId", userId);

        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double shippingFee = 50000.0;
        double totalWithShipping = totalPrice + shippingFee;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalWithShipping", totalWithShipping);

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

    // When user clicks "checkout" on the cart page
    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during checkout start: {}", session.getId());

        // If not logged in, redirect to login page
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // Get user information
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();

        // Subtract item quantities from stock
        cartService.subtractItemQuantitiesFromStock(userId);

        // Store user ID in session for item restoration if needed
        session.setAttribute("userId", userId);
        session.setAttribute("checkoutStartTime", LocalDateTime.now());

        return "redirect:/checkout"; // Proceed to checkout page
    }

    // When user cancels checkout
    @PostMapping("/checkout/cancel")
    public ResponseEntity<Void> cancelCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during cancel: {}", session.getId());

        // If not logged in, return 401 Unauthorized
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get user information
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();

        // Restore item quantities in stock
        cartService.restoreItemQuantitiesToStock(userId);

        // Remove checkout session attributes
        session.removeAttribute("checkoutStartTime");
        session.removeAttribute("userId");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm-checkout")
    public String confirmCheckout(HttpSession session, Principal principal) {

        // If not logged in, redirect to login page
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // Get checkout start time
        LocalDateTime checkoutStartTime = (LocalDateTime) session.getAttribute("checkoutStartTime");

        // If checkout start time exists, check for expiration
        if (checkoutStartTime != null) {
            if (Duration.between(checkoutStartTime, LocalDateTime.now()).toMinutes() > 30) {
                Integer userId = userService.findByEmail(principal.getName()).getUserId();
                cartService.restoreItemQuantitiesToStock(userId);

                // Remove checkout session attributes
                session.removeAttribute("checkoutStartTime");
                session.removeAttribute("userId");

                // Redirect to cart with expiration notice
                return "redirect:/cart?checkoutExpired=true";
            }
        }
        return "redirect:/complete-checkout"; // Redirect to checkout completion (not implemented)
    }

    // Endpoint to check session status for JavaScript polling
    @GetMapping("/session-status")
    @ResponseBody
    public Map<String, Boolean> checkSessionStatus(HttpSession session) {
        Map<String, Boolean> response = new HashMap<>();
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");

        logger.info("Checking session status for session ID: {}", session.getId());

        if (sessionStartTime == null) {
            logger.info("Session start time is null. Clearing checkout attributes.");
            session.removeAttribute("sessionStartTime");
            session.removeAttribute("userId");
            response.put("sessionValid", false);
            return response;
        }

        logger.info("Session started at: {}", sessionStartTime);
        Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
        logger.info("Session duration: {} minutes", duration.toMinutes());

        // Check if the session duration has exceeded the timeout
        if (duration.toMinutes() >= SESSION_TIMEOUT_MINUTES) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                logger.info("Session expired for user ID: {}. Restoring item quantities.", userId);
                cartService.restoreItemQuantitiesToStock(userId);
                logger.info("Items restored for user ID: {}", userId);
            } else {
                logger.warn("User ID not found in session. Unable to restore items.");
            }
            // Clear checkout-related session attributes but do not invalidate the entire session
            session.removeAttribute("sessionStartTime");
            session.removeAttribute("userId");
            response.put("sessionValid", false);
            logger.info("Cleared checkout attributes for session ID: {}", session.getId());
        } else {
            response.put("sessionValid", true);
        }

        return response;
    }
}