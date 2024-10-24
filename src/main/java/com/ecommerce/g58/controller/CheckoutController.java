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
    private ProductImageRepository productImageRepository; // dung de lay anh san pham

    @GetMapping("/session-info")
    public ResponseEntity<String> getSessionInfo(HttpSession session) {
        // lay thong tin session
        StringBuilder sessionInfo = new StringBuilder();
        sessionInfo.append("Session ID: ").append(session.getId()).append("\n");

        // lay thoi gian hien tai de kiem tra thoi gian bat dau checkout
        LocalDateTime checkoutTime = (LocalDateTime) session.getAttribute("checkoutSession");
        // neu co thoi gian bat dau checkout, hien thi thong tin
        if (checkoutTime != null) {
            sessionInfo.append("Checkout Session Start Time: ").append(checkoutTime).append("\n");
        } else {
            sessionInfo.append("No checkout session active.\n");
        }
        // tra ve thong tin session
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

        logger.info("Proceed to checkout called successfully for user: {}", userId);

        // tru so luong san pham trong kho
        cartService.subtractItemQuantitiesFromStock(userId);

        // tao session de luu thoi gian bat dau checkout
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

        // tra lai so luong san pham trong kho
        cartService.restoreItemQuantitiesToStock(userId);

        // kiem tra xem co session checkout hay khong
        if (session.getAttribute("checkoutStartTime") != null) {
            logger.info("Checkout session found, canceling checkout for user: {}", username);
        } else {
            logger.warn("No checkout session found for user: {}", username);
        }

        // xoa session checkout
        session.removeAttribute("checkoutStartTime");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm-checkout")
    public String confirmCheckout(HttpSession session, Principal principal) {
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // lay thoi gian bat dau checkout
        LocalDateTime checkoutStartTime = (LocalDateTime) session.getAttribute("checkoutStartTime");

        if (checkoutStartTime != null) {
            // neu checkout qua 30 phut, tra lai so luong san pham trong kho
            if (Duration.between(checkoutStartTime, LocalDateTime.now()).toMinutes() > 30) {
                Integer userId = userService.findByEmail(principal.getName()).getUserId();
                cartService.restoreItemQuantitiesToStock(userId);

                // xoa session checkout
                session.removeAttribute("checkoutStartTime");

                // chuyen huong ve trang gio hang voi thong bao checkout het han
                return "redirect:/cart?checkoutExpired=true";
            }
        }

        return "redirect:/payment"; // chuyen huong ve trang thanh toan ( chua co)
    }
}