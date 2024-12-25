package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.*;
import com.ecommerce.g58.service.implementation.ProfileService;
import com.ecommerce.g58.service.implementation.VNPayService;
import com.ecommerce.g58.utils.FormatVND;
import com.ecommerce.g58.utils.RandomOrderCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private ShippingUnitService shippingUnitService;

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

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProfileService profileService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal, HttpSession session,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("cartItemIds") List<Integer> cartItemIds,
                                   @RequestParam(value = "shippingUnitId", required = false) Integer shippingUnitId) {


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
//                cartService.restoreItemQuantitiesToStock(userId);
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
//        cartService.subtractItemQuantitiesFromStock(userId);

        // Lấy giỏ hàng của người dùng
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();
        List<CartItem> cartItemss = cartItemService.getCartItemsByIds(cartItemIds);
        logger.info("User cart items retrieved. Total items: {}", cartItems.size());

        // Tính tổng giá giỏ hàng
        long totalPrice = cartItems.stream()
                .mapToLong(item -> (long) item.getPrice() * item.getQuantity())
                .sum();
        logger.info("Total cart price calculated: {}", totalPrice);

        double totalWeight = cartItems.stream()
                .mapToDouble(item -> item.getProductId().getWeight() * item.getQuantity())
                .sum();

        totalWeight = Math.max(totalWeight, 1.0);
        // Lấy tất cả các đơn vị vận chuyển để hiển thị trong dropdown
        List<ShippingUnit> shippingUnits = shippingUnitService.getAllShippingUnits();
        model.addAttribute("shippingUnits", shippingUnits);
        // Lấy phí vận chuyển từ bảng shipping_unit dựa trên shippingUnitId
        long shippingFee = (long) (shippingUnits.get(0).getUnitPrice().longValue() * totalWeight);
        if (shippingUnitId != null) {
            Optional<ShippingUnit> shippingUnitOpt = shippingUnitService.findById(shippingUnitId);
            if (shippingUnitOpt.isPresent()) {
                shippingFee = Math.round(shippingUnitOpt.get().getUnitPrice().longValue() * totalWeight);
                model.addAttribute("selectedShippingUnit", shippingUnitOpt.get());
            } else {
                shippingFee = (long) (shippingUnits.get(0).getUnitPrice().longValue() * totalWeight);
                model.addAttribute("errorMessage", "Invalid shipping unit selected.");
            }

        }

        double taxRate = 0.08; // 8% tax rate
        long baseTotal = totalPrice + shippingFee; // Total before tax
        long taxAmount = (long) (totalPrice * taxRate); // Tax applied to the base total
        long totalWithShipping = baseTotal + taxAmount; // Final total including tax

// Debugging logs
        logger.info("Base total (price + shipping): {}", baseTotal);
        logger.info("Tax amount calculated: {}", taxAmount);
        logger.info("Total with shipping and tax: {}", totalWithShipping);

        model.addAttribute("user", user);
        model.addAttribute("tax", taxAmount);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalWithShipping", totalWithShipping);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("cartItemIds", cartItemIds);

        model.addAttribute("totalWeight", totalWeight);


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
        model.addAttribute("walletBalance", FormatVND.formatCurrency(BigDecimal.valueOf(walletBalance)));
        logger.info("Wallet balance retrieved for user ID {}: {}", user.getUserId(), walletBalance);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal, @ModelAttribute Orders order, Model model,
                                    @RequestParam("formattedShippingAddress") String formattedShippingAddress,
                                    @RequestParam("cartItemIdsString") String cartItemIdsString,
                                    @RequestParam(value = "shippingUnitId") Integer shippingUnitId,
                                    @RequestParam(value = "paymentMethod", defaultValue = "WALLET") PaymentMethod paymentMethod,
                                    @RequestParam(value = "paymentType", defaultValue = "full") String paymentType,
                                    RedirectAttributes redirectAttributes) {
        List<Integer> cartItemIds = Arrays.stream(cartItemIdsString.replaceAll("\\[|\\]", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())  // Filter out empty strings
                .map(Integer::parseInt)
                .collect(Collectors.toList());
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

        // Tính tổng giá giỏ hàng
        long totalPrice = cartItems.stream()
                .mapToLong(item -> (long) item.getPrice() * item.getQuantity())
                .sum();
        logger.info("Total cart price calculated: {}", totalPrice);

        // Tính phí vận chuyển dựa trên đơn vị vận chuyển được chọn
        double totalWeight = cartItems.stream()
                .mapToDouble(item -> item.getProductId().getWeight() * item.getQuantity())
                .sum();

        totalWeight = Math.max(totalWeight, 1.0);
        // Lấy tất cả các đơn vị vận chuyển để hiển thị trong dropdown


        List<ShippingUnit> shippingUnits = shippingUnitService.getAllShippingUnits();
        long shippingFee = (long) (shippingUnits.get(0).getUnitPrice().longValue() * totalWeight);
        if (shippingUnitId != null) {
            Optional<ShippingUnit> shippingUnitOpt = shippingUnitService.findById(shippingUnitId);
            if (shippingUnitOpt.isPresent()) {
                shippingFee = Math.round(shippingUnitOpt.get().getUnitPrice().longValue() * totalWeight);
                model.addAttribute("selectedShippingUnit", shippingUnitOpt.get());
            } else {
                return "checkout";
            }

        }

        // Calculate the final total based on payment type
        long totalWithShipping;
        double tax = 0.08;
        Invoice invoice = new Invoice();

        if ("deposit".equalsIgnoreCase(paymentType)) {
            totalWithShipping = (long) ((((double) totalPrice / 2) + shippingFee) + (totalPrice * tax)); // 50% of total price, full shipping fee

            invoice.setDeposit(BigDecimal.valueOf(totalWithShipping));
            invoice.setShippingFee(BigDecimal.valueOf(shippingFee));
            invoice.setTotalAmount(BigDecimal.valueOf(totalPrice));
            invoice.setTax(BigDecimal.valueOf(totalPrice * tax));
            invoice.setRemainingBalance(BigDecimal.valueOf(totalPrice - (totalPrice / 2)));

        } else {
            totalWithShipping = (long) ((totalPrice + shippingFee) + (totalPrice * tax));
            invoice.setDeposit(BigDecimal.valueOf(totalWithShipping));
            invoice.setTotalAmount(BigDecimal.valueOf(totalPrice));
            invoice.setShippingFee(BigDecimal.valueOf(shippingFee));
            invoice.setTax(BigDecimal.valueOf(totalPrice * tax));
            invoice.setRemainingBalance(BigDecimal.valueOf(0));
        }
        logger.info("Total with shipping calculated based on payment type '{}': {}", paymentType, totalWithShipping);

        // Handle payment methods using PaymentMethod enum
        for (CartItem item : cartItems) {
            ProductVariation variation = item.getVariationId(); // Retrieve the selected ProductVariation
            int cartQuantity = item.getQuantity();
            int availableStock = variation.getStock(); // Stock is now validated at the variation level

            if (cartQuantity > availableStock) {
                String encodedCartItemIds = URLEncoder.encode(cartItemIdsString.replaceAll("\\[|\\]", ""), StandardCharsets.UTF_8);

                logger.warn("Insufficient stock for product variation ID {}. Cart quantity: {}, Available stock: {}",
                        variation.getVariationId(), cartQuantity, availableStock);
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm \"" + variation.getProductId().getProductName() +
                        "\" (Màu: " + variation.getColor().getColorName() + ", Kích thước: " + variation.getSize().getSizeName() +
                        ") không đủ hàng trong kho. Vui lòng giảm số lượng hoặc xóa sản phẩm này khỏi giỏ hàng.");
                redirectAttributes.addFlashAttribute("cartItems", cartItems);
                return "redirect:/checkout?cartItemIds=" + encodedCartItemIds; // Return to the checkout page with an error
            }
        }
        // Trừ số lượng sản phẩm và bắt đầu quá trình checkout


        if (paymentMethod == PaymentMethod.WALLET) {
            cartService.subtractItemQuantitiesFromStock(userId);
            long walletBalance = walletService.getUserWalletBalance(userId);
            logger.info("Wallet balance retrieved for user ID {}: {}", userId, walletBalance);

            // Kiểm tra nếu số dư ví không đủ
            if (walletBalance < totalWithShipping) {
                long taxAmount = (long) (totalPrice * tax); // Tax applied to the base total
                model.addAttribute("errorMessage1", "Bạn không đủ số dư để thực hiện giao dịch. Vui lòng nạp thêm tiền vào ví hoặc chọn phương thức thanh toán khác.");
                model.addAttribute("totalWithShipping", totalWithShipping);
                model.addAttribute("walletBalance", FormatVND.formatCurrency(BigDecimal.valueOf(walletBalance)));
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("shippingFee", shippingFee);
                model.addAttribute("shippingUnits", shippingUnits);
                model.addAttribute("tax", taxAmount);
                cartService.restoreItemQuantitiesToStock(userId);
                return "checkout"; // Quay lại trang checkout nếu số dư ví không đủ
            }


            // **Check product stock before proceeding**

            session.setAttribute("userId", userId);
            session.setAttribute("checkoutStartTime", LocalDateTime.now());
            logger.info("Stock quantities updated and checkout started for user ID {}.", userId);
            // Trừ tiền trong ví của người mua
            walletService.deductFromWallet(userId, totalWithShipping, paymentType);
            logger.info("Đã trừ số dư ví cho người dùng ID {}. Số tiền trừ: {}. Loại thanh toán: {}", userId, totalWithShipping, paymentType);
            logger.info(formattedShippingAddress + "s");


            // Xác nhận và lưu đơn hàng
            order.setUserId(user);
            order.setShippingAddress(formattedShippingAddress);
            order.setOrderDate(LocalDateTime.now());
            order.setTotalPrice(totalWithShipping);
            logger.info("Order prepared for user ID {}. Total with shipping: {}", userId, totalWithShipping);

            // Lưu đơn hàng vào cơ sở dữ liệu
            order.setOrderCode(RandomOrderCodeGenerator.generateOrderCode());
            orderRepository.save(order);
            logger.info("Order saved in database with ID: {}", order.getOrderId());

            walletService.addToWalletForAdmin(totalWithShipping, paymentType, order);
            logger.info("Đã cộng {} vào ví ADMIN. Loại thanh toán: {}", totalWithShipping, paymentType);

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
            invoice.setOrderId(order);
            invoiceRepository.save(invoice);
            model.addAttribute("paymentMethod", paymentMethod);
            logger.info("Order details added to model for display on checkout-complete page.");

            // Chuyển hướng về trang checkout-complete
            return "checkout-complete";
        } else if (paymentMethod == PaymentMethod.VNPAY) {
            session.setAttribute("userOrderOn", user);
            session.setAttribute("paymentType", paymentType);
            session.setAttribute("totalWeight", totalWeight);
            session.setAttribute("shippingUnitId", shippingUnitId);
            session.setAttribute("addressOrderOn", formattedShippingAddress);
            session.setAttribute("paymentMethodOrderOn", paymentMethod);
            session.setAttribute("cartItemIdsOrderOn", cartItemIds);
            model.addAttribute("totalOrderPrice", totalWithShipping);
            return "create-vnpay-order";
        } else {
            logger.info("Phương thức thanh toán không hợp lệ.");
            model.addAttribute("errorMessage", "Phương thức thanh toán không hợp lệ.");
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
//        cartService.restoreItemQuantitiesToStock(userId);
//        logger.info("Item quantities restored for user ID {}.", userId);

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
//                cartService.restoreItemQuantitiesToStock(userId);
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