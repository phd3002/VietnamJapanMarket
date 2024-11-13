package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.OrderDetailRepository;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.repository.ShippingStatusRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import com.ecommerce.g58.service.implementation.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private static final int SESSION_TIMEOUT_MINUTES = 30; // set = 1 neu test

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private CartService cartService;

    @Autowired
    private StoreService storeService;

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
    private WalletService walletService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal, HttpSession session,
                                   @RequestParam(value = "shippingMethod", defaultValue = "standard") String shippingMethod) {
        // Lấy thời gian bắt đầu session
        LocalDateTime sessionStartTime = (LocalDateTime) session.getAttribute("sessionStartTime");

        // Nếu session chưa có thời gian bắt đầu, tạo mới và lưu vào session
        if (sessionStartTime == null) {
            sessionStartTime = LocalDateTime.now();
            session.setAttribute("sessionStartTime", sessionStartTime);
            logger.info("Session start time set to: {}", sessionStartTime);
        }

        // Tính toán thời gian của session
        Duration duration = Duration.between(sessionStartTime, LocalDateTime.now());
        logger.info("Session duration calculated: {} minutes", duration.toMinutes());

        // Kiểm tra nếu session vượt qua thời gian timeout
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

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (principal == null) {
            logger.warn("User not authenticated. Redirecting to sign-in.");
            return "redirect:/sign-in";
        }

        // Lấy thông tin người dùng
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            logger.warn("User not found for username: {}. Redirecting to sign-in.", username);
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        session.setAttribute("userId", userId);
        logger.info("User ID set in session: {}", userId);

        // Retrieve store information by owner ID
        Optional<Stores> storeOpt = storeService.findByOwnerId(user);
        if (storeOpt.isPresent()) {
            model.addAttribute("store", storeOpt.get());
            logger.info("Store found for user ID {}: {}", userId, storeOpt.get().getStoreName());
        } else {
            logger.warn("Store not found for user ID: {}", userId);
        }

        // Gọi hàm trừ số lượng sản phẩm trong kho khi bắt đầu quá trình thanh toán
        cartService.subtractItemQuantitiesFromStock(userId);

        // Lấy giỏ hàng của người dùng
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();
        logger.info("User cart items retrieved. Total items: {}", cartItems.size());

        // Tính tổng giá giỏ hàng
        long totalPrice = cartItems.stream()
                .mapToLong(item -> (long) item.getPrice() * item.getQuantity())
                .sum();
        logger.info("Total cart price calculated: {}", totalPrice);

        // Tính phí vận chuyển dựa trên phương thức vận chuyển
        long shippingFee;
        switch (shippingMethod) {
            case "express":
                shippingFee = 100000;
                break;
            case "same-day":
                shippingFee = 150000;
                break;
            default: // standard
                shippingFee = 50000;
                break;
        }
        logger.info("Shipping fee calculated for method '{}': {}", shippingMethod, shippingFee);

        long totalWithShipping = totalPrice + shippingFee;
        logger.info("Total with shipping calculated: {}", totalWithShipping);

        // Thêm thông tin vào model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalWithShipping", totalWithShipping);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("selectedShippingMethod", shippingMethod);

        // Lấy hình ảnh cho từng sản phẩm trong giỏ hàng
        Map<Integer, String> productImages = new HashMap<>();
        for (CartItem item : cartItems) {
            List<ProductImage> images = productImageRepository.findByProductProductId(item.getProductId().getProductId());
            if (!images.isEmpty()) {
                productImages.put(item.getProductId().getProductId(), images.get(0).getThumbnail());
                logger.info("Thumbnail set for product ID {}: {}", item.getProductId().getProductId(), images.get(0).getThumbnail());
            }
        }
        model.addAttribute("productImages", productImages);

        // Lấy số dư ví của người dùng
        long walletBalance = walletService.getUserWalletBalance(user.getUserId());
        model.addAttribute("walletBalance", walletBalance);
        logger.info("Wallet balance retrieved for user ID {}: {}", user.getUserId(), walletBalance);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal, @ModelAttribute Orders order, Model model,
                                    @RequestParam(value = "shippingMethod", defaultValue = "standard") String shippingMethod,
                                    @RequestParam(value = "paymentMethod", defaultValue = "wallet") String paymentMethod) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (principal == null) {
            logger.warn("User not authenticated. Redirecting to sign-in.");
            return "redirect:/sign-in";
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            logger.warn("User not found for username: {}. Redirecting to sign-in.", username);
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();
        logger.info("User cart items retrieved for user ID {}. Total items: {}", userId, cartItems.size());

        // Log the payment method to check its value
        logger.info("Payment method selected: {}", paymentMethod);

        // Tính tổng giá giỏ hàng
        long totalPrice = cartItems.stream()
                .mapToLong(item -> (long) item.getPrice() * item.getQuantity())
                .sum();
        logger.info("Total cart price calculated: {}", totalPrice);

        // Tính phí vận chuyển dựa trên phương thức vận chuyển
        long shippingFee;
        switch (shippingMethod) {
            case "express":
                shippingFee = 100000;
                break;
            case "same-day":
                shippingFee = 150000;
                break;
            default: // standard
                shippingFee = 50000;
                break;
        }
        logger.info("Shipping fee calculated for method '{}': {}", shippingMethod, shippingFee);

        long totalWithShipping = totalPrice + shippingFee;
        logger.info("Total with shipping calculated: {}", totalWithShipping);

        // Nếu phương thức thanh toán là "wallet", kiểm tra số dư ví
        if ("wallet".equalsIgnoreCase(paymentMethod)) {
            long walletBalance = walletService.getUserWalletBalance(userId);
            logger.info("Wallet balance retrieved for user ID {}: {}", userId, walletBalance);

            // Kiểm tra nếu số dư ví không đủ
            if (walletBalance < totalWithShipping) {
                // Thêm thông báo lỗi vào model và trả về trang checkout
                model.addAttribute("errorMessage", "Bạn không đủ số dư để thực hiện giao dịch.");
                model.addAttribute("totalWithShipping", totalWithShipping);
                model.addAttribute("walletBalance", walletBalance);
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("shippingFee", shippingFee);
                return "checkout"; // Quay lại trang checkout nếu số dư ví không đủ
            }

            // Trừ tiền trong ví của người mua
            walletService.deductFromWallet(userId, totalWithShipping);
            logger.info("Wallet balance deducted for user ID {}. Amount deducted: {}", userId, totalWithShipping);

            // Lấy thực thể Stores từ sản phẩm trong giỏ hàng
            Stores store = cartItems.get(0).getProductId().getStoreId(); // Lấy đối tượng Stores

            // Lấy storeId từ đối tượng Stores và tìm cửa hàng trong database
            Integer storeId = store.getStoreId();
            Optional<Stores> optionalStore = storeService.findById(storeId);

            // Cộng số tiền thanh toán vào ví của cửa hàng
            if (optionalStore.isPresent()) {
                Stores foundStore = optionalStore.get();
                walletService.addToWallet(foundStore.getOwnerId().getUserId(), totalWithShipping); // Sử dụng ownerId để cộng vào ví của chủ cửa hàng
                logger.info("Cộng {} vào ví của cửa hàng có ID {}", totalWithShipping, storeId);
            }
        } else if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            String returnUrl = "http://yourdomain.com/vnpay-payment";
            String paymentUrl = vnPayService.createPaymentUrl(order.getOrderId(), (int) totalWithShipping, PaymentMethod.VNPAY, returnUrl);
            session.setAttribute("orderPending", order);
            return "redirect:" + paymentUrl;
        } else {
            logger.info("Phương thức thanh toán là COD. Không thực hiện trừ ví.");
        }

        // Trừ số lượng sản phẩm và bắt đầu quá trình checkout
        cartService.subtractItemQuantitiesFromStock(userId);
        session.setAttribute("userId", userId);
        session.setAttribute("checkoutStartTime", LocalDateTime.now());
        logger.info("Stock quantities updated and checkout started for user ID {}.", userId);

        // Xác nhận và lưu đơn hàng
        order.setUserId(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(totalWithShipping);
        logger.info("Order prepared for user ID {}. Total with shipping: {}", userId, totalWithShipping);

        // Lưu đơn hàng vào cơ sở dữ liệu
        orderRepository.save(order);
        logger.info("Order saved in database with ID: {}", order.getOrderId());

        // Tạo trạng thái giao hàng mới cho đơn hàng (mặc định là pending)
        ShippingStatus initialShippingStatus = ShippingStatus.builder()
                .orderId(order)
                .status("Pending")
                .updatedAt(LocalDateTime.now())
                .build();

        // Lưu trạng thái giao hàng vào cơ sở dữ liệu
        shippingStatusRepository.save(initialShippingStatus);
        logger.info("Initial shipping status set to 'Pending' and saved for order ID: {}", order.getOrderId());

        // Thêm trạng thái giao hàng vào đơn hàng
        List<ShippingStatus> shippingStatuses = new ArrayList<>();
        shippingStatuses.add(initialShippingStatus);
        order.setShippingStatus(shippingStatuses);

        // Lưu đơn hàng với trạng thái đơn hàng vào cơ sở dữ liệu
        orderRepository.save(order);
        logger.info("Order with shipping status saved again for order ID: {}", order.getOrderId());

        // Lưu chi tiết đơn hàng vào cơ sở dữ liệu
        for (CartItem item : cartItems) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(order);
            orderDetails.setProductId(item.getProductId());
            orderDetails.setVariationId(item.getVariationId());
            orderDetails.setQuantity(item.getQuantity());
            orderDetails.setPrice(item.getPrice());
            orderDetailRepository.save(orderDetails);
            logger.info("Order detail saved for product ID {} with quantity {}.", item.getProductId().getProductId(), item.getQuantity());
        }

        // Xóa tất cả sản phẩm trong giỏ hàng sau khi đã đặt hàng
        cartService.clearCart(user.getUserId());
        logger.info("Cart cleared for user ID {} after order placement.", user.getUserId());

        // Đặt đơn hàng cho model để hiển thị thông tin đơn hàng
        model.addAttribute("order", order);
        model.addAttribute("paymentMethod", paymentMethod);
        logger.info("Order details added to model for display on checkout-complete page.");

        // Chuyển hướng về trang checkout-complete
        return "checkout-complete";
    }

    @GetMapping("/vnpay-payment")
    public String vnpayPaymentReturn(HttpSession session, HttpServletRequest request, Model model) {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

        String result = vnPayService.handlePaymentReturn(params);
        Orders order = (Orders) session.getAttribute("orderPending");

        if ("Thanh toán thành công".equals(result) && order != null) {
            orderRepository.save(order);
            logger.info("Order confirmed and saved in database with ID: {}", order.getOrderId());
            session.removeAttribute("orderPending");
            cartService.clearCart(order.getUserId().getUserId());
            logger.info("Cart cleared for user ID {} after successful payment.", order.getUserId().getUserId());
            model.addAttribute("order", order);
            return "checkout-complete";
        } else {
            model.addAttribute("errorMessage", result);
            return "checkout";
        }
    }

    @PostMapping("/checkout/cancel")
    public ResponseEntity<Void> cancelCheckout(HttpSession session, Principal principal) {
        // Log session ID khi người dùng bấm hủy
        logger.info("Session ID during cancel: {}", session.getId());

        // Kiểm tra nếu người dùng chưa đăng nhập, trả về 401 Unauthorized
        if (principal == null) {
            logger.warn("Unauthorized cancel attempt. User not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            logger.warn("Unauthorized cancel attempt. User not found for username: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();
        logger.info("User ID {} is canceling checkout.", userId);

        // Khôi phục số lượng sản phẩm trong kho cho giỏ hàng người dùng
        cartService.restoreItemQuantitiesToStock(userId);
        logger.info("Item quantities restored for user ID {}.", userId);

        // Xóa các thuộc tính liên quan đến checkout khỏi session
        session.removeAttribute("checkoutStartTime");
        session.removeAttribute("userId");
        logger.info("Checkout-related session attributes removed for user ID {}.", userId);

        // Trả về phản hồi thành công 200 OK
        return ResponseEntity.ok().build();
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
}