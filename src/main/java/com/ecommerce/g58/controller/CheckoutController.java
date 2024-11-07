package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.OrderDetailRepository;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.repository.ShippingStatusRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private static final int SESSION_TIMEOUT_MINUTES = 30; // set = 1 neu test

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    @Autowired
    private WalletService walletService; // Ensure WalletService is defined and injected


    // Hien thi man checkout
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal, HttpSession session) {
        // lay thoi gian bat dau session
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");

        if (sessionStartTime == null) {
            sessionStartTime = LocalDateTime.now();
            session.setAttribute("sessionStartTime", sessionStartTime);
        }

        // Tinh thoi gian session
        Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
        logger.info("Session duration for ID {}: {} minutes", session.getId(), duration.toMinutes());

        // Neu session qua thoi gian timeout, invalidate session va redirect ve homepage
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

        double walletBalance = walletService.getUserWalletBalance(user.getUserId());
        model.addAttribute("walletBalance", walletBalance);
        return "checkout";
    }

    @GetMapping("/calculate-shipping")
    @ResponseBody
    public Map<String, Double> calculateShipping(@RequestParam("shippingMethod") String shippingMethod,
                                                 Principal principal) {
        double shippingFee;
        switch (shippingMethod) {
            case "express":
                shippingFee = 100000.0;
                break;
            case "same-day":
                shippingFee = 150000.0;
                break;
            default: // standard
                shippingFee = 50000.0;
                break;
        }

        Users user = userService.findByEmail(principal.getName());
        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);

        double totalPrice = userCart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double totalWithShipping = totalPrice + shippingFee;

        Map<String, Double> response = new HashMap<>();
        response.put("totalWithShipping", totalWithShipping);
        response.put("shippingFee", shippingFee);

        return response;
    }

    // khi nguoi dung bam nut checkout
    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal, Model model,
                                    @RequestParam(value = "shippingMethod", defaultValue = "standard") String shippingMethod,
                                    @RequestParam(value = "paymentMethod", defaultValue = "wallet") String paymentMethod) {
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

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Determine shipping fee based on the selected method
        double shippingFee;
        switch (shippingMethod) {
            case "express":
                shippingFee = 100000.0;
                break;
            case "same-day":
                shippingFee = 150000.0;
                break;
            default: // standard
                shippingFee = 50000.0;
                break;
        }

        double totalWithShipping = totalPrice + shippingFee;

        // Nếu phương thức thanh toán không phải là COD, kiểm tra số dư ví
        if (!"cod".equalsIgnoreCase(paymentMethod)) {
            double walletBalance = walletService.getUserWalletBalance(userId);
            if (walletBalance < totalWithShipping) {
                model.addAttribute("totalWithShipping", totalWithShipping);
                model.addAttribute("walletBalance", walletBalance);
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("shippingFee", shippingFee);
                return "checkout"; // Stay on checkout page with error message
            }
        }

        // Deduct items from stock and proceed with checkout
        cartService.subtractItemQuantitiesFromStock(userId);
        session.setAttribute("userId", userId);
        session.setAttribute("checkoutStartTime", LocalDateTime.now());

        return "redirect:/confirm-checkout"; // Redirect to confirm-checkout for final session validation
    }

    // khi nguoi dung bam nut cancel
    @PostMapping("/checkout/cancel")
    public ResponseEntity<Void> cancelCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during cancel: {}", session.getId());

        // neu chua dang nhap, tra ve 401 Unauthorized
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();

        cartService.restoreItemQuantitiesToStock(userId);

        // xoa cac thuoc tinh checkout khoi session
        session.removeAttribute("checkoutStartTime");
        session.removeAttribute("userId");

        return ResponseEntity.ok().build();
    }

    // xac nhan checkout
    @GetMapping("/confirm-checkout")
    public String confirmCheckout(HttpSession session, Principal principal) {

        if (principal == null) {
            return "redirect:/sign-in";
        }

        // lay thoi gian bat dau checkout tu session
        LocalDateTime checkoutStartTime = (LocalDateTime) session.getAttribute("checkoutStartTime");

        // neu thoi gian checkout > 30 phut, restore so luong san pham
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
        return "redirect:/complete-checkout"; // redirect ve trang complete-checkout (chua co)
    }

    // endpoint kiem tra trang thai session
    @GetMapping("/session-status")
    @ResponseBody
    public Map<String, Boolean> checkSessionStatus(HttpSession session) {
        Map<String, Boolean> response = new HashMap<>();
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");
        logger.info("Checking session status for session ID: {}", session.getId());

        // neu sessionStartTime = null, xoa cac thuoc tinh checkout khoi session
        if (sessionStartTime == null) {
            logger.info("Session start time is null. Clearing checkout attributes.");
            session.removeAttribute("sessionStartTime");
            session.removeAttribute("userId");
            response.put("sessionValid", false);
            return response;
        }

        // Log session start time and duration
        logger.info("Session started at: {}", sessionStartTime);
        Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
        logger.info("Session duration: {} minutes", duration.toMinutes());

        // kiem tra thoi gian session > 30 phut, restore so luong san pham
        if (duration.toMinutes() >= SESSION_TIMEOUT_MINUTES) {
            // lay userId tu session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                logger.info("Session expired for user ID: {}. Restoring item quantities.", userId);
                cartService.restoreItemQuantitiesToStock(userId);
                logger.info("Items restored for user ID: {}", userId);
            } else {
                logger.warn("User ID not found in session. Unable to restore items.");
            }
            // xoa cac thuoc tinh checkout khoi session
            session.removeAttribute("sessionStartTime");
            session.removeAttribute("userId");
            response.put("sessionValid", false);
            logger.info("Cleared checkout attributes for session ID: {}", session.getId());
        } else {
            response.put("sessionValid", true);
        }

        return response;
    }

    // endpoint lay thoi gian con lai cua session
    @GetMapping("/checkout-time-remaining")
    @ResponseBody
    public Map<String, Object> getTimeRemaining(HttpSession session) {
        // lay thoi gian bat dau session
        Map<String, Object> response = new HashMap<>();
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");

        // neu sessionStartTime != null, tinh thoi gian con lai va tra ve ket qua
        if (sessionStartTime != null) {
            Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
            long minutesElapsed = duration.toMinutes();

            // kiem tra thoi gian da qua 25 phut, hien thi warning
            response.put("minutesElapsed", minutesElapsed);
            response.put("showWarning", minutesElapsed >= 25 && minutesElapsed < SESSION_TIMEOUT_MINUTES);
        } else {
            response.put("showWarning", false);
        }
        // tra ve ket qua
        return response;
    }

    // endpoint xac nhan checkout
    @PostMapping("/process-checkout")
    public String processCheckout(@ModelAttribute Orders order, Principal principal, HttpSession session, Model model) {
        // kiem tra xem nguoi dung da dang nhap
        Users user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/sign-in";
        }

        // set thong tin cho order
        order.setUserId(user);
        order.setOrderDate(LocalDateTime.now());

        // lay tong tien cua don hang
        Double totalWithShipping = (Double) model.getAttribute("totalWithShipping");
        if (totalWithShipping == null) {
            List<CartItem> cartItems = cartService.getCartByUserId(user.getUserId()).getCartItems();
            double totalPrice = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            double shippingFee = 50000.0;
            totalWithShipping = totalPrice + shippingFee;
        }
        order.setTotalPrice(totalWithShipping);

        // luu order vao database
        orderRepository.save(order);

        // tao shipping status moi cho order (mac dinh la pending)
        ShippingStatus initialShippingStatus = ShippingStatus.builder()
                .orderId(order)
                .status("Pending")
                .updatedAt(LocalDateTime.now())
                .build();
        // luu shipping status vao database
        shippingStatusRepository.save(initialShippingStatus);

        // them shipping status vao order
        List<ShippingStatus> shippingStatuses = new ArrayList<>();
        shippingStatuses.add(initialShippingStatus);
        order.setShippingStatus(shippingStatuses);

        // luu order voi trang thai don hang vao database
        orderRepository.save(order);

        // luu order details vao database
        List<CartItem> cartItems = cartService.getCartByUserId(user.getUserId()).getCartItems();
        for (CartItem item : cartItems) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(order);
            orderDetails.setProductId(item.getProductId());
            orderDetails.setVariationId(item.getVariationId());
            orderDetails.setQuantity(item.getQuantity());
            orderDetails.setPrice(item.getPrice());
            orderDetailRepository.save(orderDetails);
        }

        // xoa tat ca san pham trong gio hang sau khi da dat hang
        cartService.clearCart(user.getUserId());

        // set order cho model de hien thi thong tin don hang
        model.addAttribute("order", order);

        // chuyen huong ve trang checkout-complete
        return "checkout-complete";
    }
}