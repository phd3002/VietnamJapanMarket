package com.ecommerce.g58.controller;

import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.service.implementation.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/create")
    public String createPayment(@RequestParam("orderId") int orderId, Model model) {
        int amount = 840; // Example value, adjust accordingly
        String returnUrl = "/vnpay-payment";
        String paymentUrl = vnPayService.createPaymentUrl(orderId, amount, PaymentMethod.VNPAY, returnUrl);

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/vnpay-payments")
    public String vnpayPaymentReturn(HttpServletRequest request, Model model) {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

        String result = vnPayService.handlePaymentReturn(params);
        model.addAttribute("message", result);
        return "checkout-complete";
    }
}

