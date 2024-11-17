package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            model.addAttribute("storeImage", firstDetail.getStoreImage());
            model.addAttribute("storeId", firstDetail.getStoreId());
            model.addAttribute("pendingTime", firstDetail.getPendingTime());
            model.addAttribute("confirmedTime", firstDetail.getConfirmedTime());
            model.addAttribute("processingTime", firstDetail.getProcessingTime());
            model.addAttribute("dispatchedTime", firstDetail.getDispatchedTime());
            model.addAttribute("shippingTime", firstDetail.getShippingTime());
            model.addAttribute("failedTime", firstDetail.getFailedTime());
            model.addAttribute("deliveredTime", firstDetail.getDeliveredTime());
            model.addAttribute("completedTime", firstDetail.getCompletedTime());
            model.addAttribute("cancelledTime", firstDetail.getCancelledTime());
            model.addAttribute("returnedTime", firstDetail.getReturnedTime());
        }

        return "order-detail"; // Thymeleaf template name
    }

    @GetMapping("/order-detail/{orderId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public void rateOrder(@PathVariable Long orderId,
                          @RequestParam String rateText,
                          @RequestParam Integer rateStar,
                          @AuthenticationPrincipal UserDetails userDetails) {
        var userEmail = userDetails.getUsername();
        orderDetailService.rateOrder(orderId, userEmail, rateText, rateStar);
    }
}
