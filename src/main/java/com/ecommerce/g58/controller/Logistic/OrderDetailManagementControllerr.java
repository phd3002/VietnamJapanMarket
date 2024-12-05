package com.ecommerce.g58.controller.Logistic;

import com.ecommerce.g58.dto.OrderDetailManagerDTO;
import com.ecommerce.g58.service.OrderDetailService;
import com.ecommerce.g58.utils.FormatVND;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderDetailManagementControllerr {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/logistic/order-detail/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        List<OrderDetailManagerDTO> orderDetails = orderDetailService.getOrderDetailsForManager(orderId);
        model.addAttribute("orderDetails", orderDetails);
        if (!orderDetails.isEmpty()) {
            OrderDetailManagerDTO firstDetail = orderDetails.get(0);
            model.addAttribute("orderId", orderId);
            model.addAttribute("storeName", firstDetail.getStoreName());
            model.addAttribute("storeAddress", firstDetail.getStoreAddress());
            model.addAttribute("storePhone", firstDetail.getStorePhone());
            model.addAttribute("orderStatus", firstDetail.getOrderStatus());
            model.addAttribute("trackingNumber", firstDetail.getOrderCode());
            model.addAttribute("updateTime", firstDetail.getStatusTime());
            model.addAttribute("customerName", firstDetail.getCustomerName());
            model.addAttribute("customerAddress", firstDetail.getCustomerAddress());
            model.addAttribute("customerPhone", firstDetail.getCustomerPhone());
            model.addAttribute("orderDate", firstDetail.getOrderDate());
            model.addAttribute("shippingFee", FormatVND.formatCurrency(BigDecimal.valueOf(firstDetail.getShippingFee())));
            model.addAttribute("tax", firstDetail.getFormattedTax());
            model.addAttribute("paymentMethod", firstDetail.getPaymentMethod());
            model.addAttribute("totalAmount", FormatVND.formatCurrency(BigDecimal.valueOf(firstDetail.getTotalAmount())));
        }
        return "logistic/order-detail";
    }
}
