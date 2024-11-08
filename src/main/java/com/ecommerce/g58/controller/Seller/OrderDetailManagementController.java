package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.OrderDetailManagerDTO;
import com.ecommerce.g58.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderDetailManagementController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/seller/order-detail/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model) {
        List<OrderDetailManagerDTO> orderDetails = orderDetailService.getOrderDetailsForManager(orderId);
        model.addAttribute("orderDetails", orderDetails);
        if (!orderDetails.isEmpty()) {
            OrderDetailManagerDTO firstDetail = orderDetails.get(0);
            model.addAttribute("orderId", orderId);
            model.addAttribute("storeName", firstDetail.getStoreName());
            model.addAttribute("storeAddress", firstDetail.getStoreAddress());
            model.addAttribute("storePhone", firstDetail.getStorePhone());
            model.addAttribute("orderStatus", firstDetail.getOrderStatus());
            model.addAttribute("trackingNumber", firstDetail.getTrackingNumber());
            model.addAttribute("updateTime", firstDetail.getStatusTime());
            model.addAttribute("customerName", firstDetail.getCustomerName());
            model.addAttribute("customerAddress", firstDetail.getCustomerAddress());
            model.addAttribute("customerPhone", firstDetail.getCustomerPhone());
            model.addAttribute("orderDate", firstDetail.getOrderDate());
            model.addAttribute("shippingFee", firstDetail.getShippingFee());
            model.addAttribute("tax", firstDetail.getTax());
            model.addAttribute("paymentMethod", firstDetail.getPaymentMethod());
            model.addAttribute("totalAmount", firstDetail.getTotalAmount());
        }
        return "seller/order-detail";
    }
}
