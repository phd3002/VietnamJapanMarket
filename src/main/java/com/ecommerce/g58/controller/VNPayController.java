package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.InvoiceRepository;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.service.*;
import com.ecommerce.g58.service.implementation.VNPayService;
import com.ecommerce.g58.utils.FormatVND;
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
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private ShippingUnitService shippingUnitService;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

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
        System.out.println("submitOrder called with totalOrderPrice: " + totalOrderPrice + ", orderInfo: " + orderInfo);

        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
            System.out.println("Using X-Forwarded-Host: " + serverHost);
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
            System.out.println("Using request URL: " + urlFixed);
        }

        String vnpayUrl = vnPayService.createOrder(totalOrderPrice, orderInfo, serverPath);
        System.out.println("VNPay URL created: " + vnpayUrl);

        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        System.out.println("Redirecting to VNPay URL");

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
        String paymentType = (String) session.getAttribute("paymentType");
        String shippingAddress = (String) session.getAttribute("addressOrderOn");
        Integer shippingUnitId = (Integer) session.getAttribute("shippingUnitId");
        Double totalWeight = (Double) session.getAttribute("totalWeight");
        PaymentMethod paymentMethod = (PaymentMethod) session.getAttribute("paymentMethodOrderOn");
        List<Integer> cartItemIds = (List<Integer>) session.getAttribute("cartItemIdsOrderOn");
        if (paymentStatus == 1) {
//            System.out.println("Payment successful");
            Orders order = orderService.createOrder(user, shippingAddress, paymentMethod, cartItemIds, shippingUnitId);
            if (order == null) {
                System.out.println("Order creation failed");
                return "error/404";
            }
            try {


                String orderCode = order.getOrderCode();
                long totalPrice = order.getTotalPrice();
                System.out.println("Order created with code: " + orderCode + ", total price: " + totalPrice);
                model.addAttribute("orderCode", orderCode);


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

                invoice.setOrderId(order);
                Orders newOrder = orderService.getOrderByCode(orderCode);
                newOrder.setTotalPrice(totalWithShipping);
                orderRepository.save(newOrder);
                System.out.println("Order Code Setting: " + newOrder.getOrderCode());
                System.out.println("New Total price: " + totalWithShipping);
                invoice.setOrderId(newOrder);
                invoiceRepository.save(invoice);
                cartItemService.removeCartItemsByIds(cartItemIds);
                walletService.addToWalletForAdmin(totalWithShipping, paymentType, order);
                model.addAttribute("totalPrice", FormatVND.formatCurrency(invoice.getDeposit()));
                return "checkout-complete-vnpay";
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error removing cart items: " + e.getMessage());
                return "error/404";
            }
        } else {
            System.out.println("Payment failed or signature verification failed");
            return "homepage";
        }
    }

    @GetMapping("/vnpay-recharge")
    public String rechargeWallet(@RequestParam("recharge-amount") int rechargeAmount,
                                 HttpServletRequest request, HttpServletResponse httpServletResponse) {

        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
            System.out.println("Using X-Forwarded-Host: " + serverHost);
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
            System.out.println("Using request URL: " + urlFixed);
        }

        String vnpayUrl = vnPayService.recharge(rechargeAmount, "orderInfo", serverPath);
        System.out.println("VNPay URL created: " + vnpayUrl);

        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        System.out.println("Redirecting to VNPay URL");

        return null;

    }

    @GetMapping("/vnpay-recharge-return")
    public String rechargeWalletReturn(HttpServletRequest request,  Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        int rechargeAmount = Integer.parseInt(request.getParameter("vnp_Amount")) / 100;

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("rechargeAmount", FormatVND.formatCurrency(BigDecimal.valueOf(rechargeAmount)));
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        System.out.println("ok");
        if (paymentStatus == 1) {
            walletService.recharge(rechargeAmount);
            model.addAttribute("message", "Recharge successful!");
            return "recharge-complete";
        } else {
            System.out.println("Nạp ok");
            model.addAttribute("errorMessage", "Nạp tiền thất bại.");
            return "redirect:/wallet";
        }
    }

    @GetMapping("/vnpay-recharge-seller")
    public String rechargeWalletSeller(@RequestParam("recharge-amount") int rechargeAmount,
                                 HttpServletRequest request, HttpServletResponse httpServletResponse) {

        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
            System.out.println("Using X-Forwarded-Host: " + serverHost);
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
            System.out.println("Using request URL: " + urlFixed);
        }

        String vnpayUrl = vnPayService.rechargeSeller(rechargeAmount, "orderInfo", serverPath);
        System.out.println("VNPay URL created: " + vnpayUrl);

        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        System.out.println("Redirecting to VNPay URL");

        return null;

    }

    @GetMapping("/vnpay-recharge-return-seller")
    public String rechargeWalletReturnSeller(HttpServletRequest request,  Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        int rechargeAmount = Integer.parseInt(request.getParameter("vnp_Amount")) / 100;

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("rechargeAmount", FormatVND.formatCurrency(BigDecimal.valueOf(rechargeAmount)));
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        System.out.println("ok");
        if (paymentStatus == 1) {
            walletService.recharge(rechargeAmount);
            model.addAttribute("message", "Recharge successful!");
            return "seller/recharge-complete-seller";
        } else {
            System.out.println("Nạp ok");
            model.addAttribute("errorMessage", "Nạp tiền thất bại.");
            return "redirect:/wallet";
        }
    }

    @GetMapping("/vnpay-recharge-logistic")
    public String rechargeWalletLogistic(@RequestParam("recharge-amount") int rechargeAmount,
                                       HttpServletRequest request, HttpServletResponse httpServletResponse) {

        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
            System.out.println("Using X-Forwarded-Host: " + serverHost);
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
            System.out.println("Using request URL: " + urlFixed);
        }

        String vnpayUrl = vnPayService.rechargeLogistic(rechargeAmount, "orderInfo", serverPath);
        System.out.println("VNPay URL created: " + vnpayUrl);

        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        System.out.println("Redirecting to VNPay URL");

        return null;

    }

    @GetMapping("/vnpay-recharge-return-logistic")
    public String rechargeWalletReturnLogistic(HttpServletRequest request,  Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        int rechargeAmount = Integer.parseInt(request.getParameter("vnp_Amount")) / 100;

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("rechargeAmount", FormatVND.formatCurrency(BigDecimal.valueOf(rechargeAmount)));
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        System.out.println("ok");
        if (paymentStatus == 1) {
            walletService.recharge(rechargeAmount);
            model.addAttribute("message", "Recharge successful!");
            return "logistic/recharge-complete-logistic";
        } else {
            System.out.println("Nạp ok");
            model.addAttribute("errorMessage", "Nạp tiền thất bại.");
            return "redirect:/wallet";
        }
    }

    @GetMapping("/vnpay-recharge-admin")
    public String rechargeWalletAdmin(@RequestParam("recharge-amount") int rechargeAmount,
                                       HttpServletRequest request, HttpServletResponse httpServletResponse) {

        String serverPath;
        String serverHost = request.getHeader("X-Forwarded-Host");
        if (!StringUtils.isEmpty(serverHost)) {
            serverPath = "https://" + serverHost;
            System.out.println("Using X-Forwarded-Host: " + serverHost);
        } else {
            String urlFixed = String.valueOf(request.getRequestURL());
            serverPath = urlFixed.replace(request.getRequestURI(), "");
            System.out.println("Using request URL: " + urlFixed);
        }

        String vnpayUrl = vnPayService.rechargeAdmin(rechargeAmount, "orderInfo", serverPath);
        System.out.println("VNPay URL created: " + vnpayUrl);

        httpServletResponse.setHeader("Location", vnpayUrl);
        httpServletResponse.setStatus(302);
        System.out.println("Redirecting to VNPay URL");

        return null;

    }

    @GetMapping("/vnpay-recharge-return-admin")
    public String rechargeWalletReturnAdmin(HttpServletRequest request,  Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        int rechargeAmount = Integer.parseInt(request.getParameter("vnp_Amount")) / 100;

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("rechargeAmount", FormatVND.formatCurrency(BigDecimal.valueOf(rechargeAmount)));
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        System.out.println("ok");
        if (paymentStatus == 1) {
            walletService.recharge(rechargeAmount);
            model.addAttribute("message", "Recharge successful!");
            return "admin/recharge-complete-admin";
        } else {
            System.out.println("Nạp ok");
            model.addAttribute("errorMessage", "Nạp tiền thất bại.");
            return "redirect:/wallet";
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

