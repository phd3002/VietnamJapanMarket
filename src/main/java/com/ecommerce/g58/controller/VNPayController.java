package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.implementation.VNPayService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/vn")
    public String pay() {
        return "redirect:/vnpay-payment";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(@RequestParam("totalOrderPrice") int totalOrderPrice,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
        }

        String vnpayUrl = vnPayService.createOrder(totalOrderPrice, orderInfo, serverPath);
        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        return null;
    }

    @GetMapping("/check-header")
    public ResponseEntity<ResponseCheckedDto> someMethod(HttpServletRequest request) {
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return ResponseEntity.ok(ResponseCheckedDto.builder().host(forwardedHost).proto(forwardedProto).fwFor(forwardedFor).build());
    }

    @GetMapping("/now")
    public @ResponseBody
    String now() {
        return "Thời gian hiện tại của hệ thống là: " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    @GetMapping("/vnpay-payment")
    public String vnpayPaymentReturn(HttpServletRequest request, Model model, HttpSession session) {
        int paymentStatus = vnPayService.orderReturn(request);
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        model.addAttribute("orderId", orderInfo);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        Users user = (Users) session.getAttribute("userOrderOn");
        String shippingAddress = (String) session.getAttribute("addressOrderOn");
        PaymentMethod paymentMethod = (PaymentMethod) session.getAttribute("paymentMethodOrderOn");
        List<Integer> cartItemIds = (List<Integer>) session.getAttribute("cartItemIdsOrderOn");
        if (paymentStatus == 1) {
            Orders order = orderService.createOrder(user, shippingAddress, paymentMethod, cartItemIds);
            if (order == null) {
                return "error/404";
            }
            try {
                cartItemService.removeCartItemsByIds(cartItemIds);
                String orderCode = order.getOrderCode();
                long totalPrice = order.getTotalPrice();
                model.addAttribute("orderCode", orderCode);
                model.addAttribute("totalPrice", totalPrice);
                return "checkout-complete-vnpay";
            } catch (Exception e) {
                e.printStackTrace();
                return "error/404";
            }
        } else {
            return "homepage";
        }
    }
    @Data
    @Builder
    private static class ResponseCheckedDto {
        private String host;
        private String proto;
        private String fwFor;
    }
}

