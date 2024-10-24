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
    private ProductImageRepository productImageRepository;

    // hien thi trang checkout
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {

        // neu chua dang nhap, chuyen huong ve trang dang nhap
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // lay thong tin user
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();

        // tinh tong tien cua gio hang
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // dat gia van chuyen la 50k
        double shippingFee = 50000.0;

        // tinh tong tien cua gio hang va van chuyen
        double totalWithShipping = totalPrice + shippingFee;

        // truyen thong tin gio hang va tong tien sang view
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

    // khi nguoi dung bam nut "checkout" tren trang gio hang
    @PostMapping("/checkout")
    public String proceedToCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during checkout start: {}", session.getId());

        // neu chua dang nhap, chuyen huong ve trang dang nhap
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // lay thong tin user
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();

        // tru so luong san pham trong kho
        cartService.subtractItemQuantitiesFromStock(userId);

        // tao session de luu thoi gian bat dau checkout
        session.setAttribute("checkoutStartTime", LocalDateTime.now());

        return "redirect:/checkout"; // Proceed to checkout page
    }

    // khi nguoi dung huy bo checkout
    @PostMapping("/checkout/cancel")
    public ResponseEntity<Void> cancelCheckout(HttpSession session, Principal principal) {
        logger.info("Session ID during cancel: {}", session.getId());

        // neu chua dang nhap, tra ve loi 401 Unauthorized
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // lay thong tin user
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();

        // tra lai so luong san pham trong kho
        cartService.restoreItemQuantitiesToStock(userId);

        // xoa session checkout
        session.removeAttribute("checkoutStartTime");

        // tra ve 200 OK
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm-checkout")
    public String confirmCheckout(HttpSession session, Principal principal) {

        // neu chua dang nhap, chuyen huong ve trang dang nhap
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // lay thoi gian bat dau checkout
        LocalDateTime checkoutStartTime = (LocalDateTime) session.getAttribute("checkoutStartTime");

        // neu da co thoi gian bat dau checkout
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
        return "redirect:/complete-checkout"; // chuyen huong ve trang hoan tat checkout (chua co)
    }
}