package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/order-detail/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model) {
        List<OrderDetailDTO> orderDetails = orderDetailService.getOrderDetails(orderId);

        model.addAttribute("orderDetails", orderDetails);

        if (!orderDetails.isEmpty()) {
            // Extract order-level details from the first entry
            OrderDetailDTO firstDetail = orderDetails.get(0);

            model.addAttribute("orderId", orderId);
            model.addAttribute("orderTotalPrice", firstDetail.getOrderTotalPrice());
            model.addAttribute("totalAmount", firstDetail.getTotalAmount());
            model.addAttribute("shippingFee", firstDetail.getShippingFee());
            model.addAttribute("paymentMethod", firstDetail.getPaymentMethod());
            model.addAttribute("paymentStatus", firstDetail.getPaymentStatus());
            model.addAttribute("shippingAddress", firstDetail.getShippingAddress());
            model.addAttribute("shippingStatus", firstDetail.getShippingStatus());
            model.addAttribute("trackingNumber", firstDetail.getTrackingNumber());
            model.addAttribute("storeName", firstDetail.getStoreName());
        }

        return "order-detail"; // Thymeleaf template name
    }
}
