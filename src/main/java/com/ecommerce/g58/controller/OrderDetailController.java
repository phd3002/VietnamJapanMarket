package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.OrderDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class OrderDetailController {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

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
            model.addAttribute("previousStatus", firstDetail.getPreviousStatus());
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

    @GetMapping("/update-order-status/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrderStatus(@PathVariable Integer orderId,
                                  @RequestParam String status,
                                  @RequestParam(required = false) String reason) {
        orderDetailService.updateStatus(orderId, status, reason);
    }

    @PostMapping("/order-detail/return")
    public String returnOrder(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("reason") String reason,
            @RequestParam("status") String status,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "Bạn cần đăng nhập để gửi yêu cầu.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/sign-in";
        }

        if (reason == null || reason.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn lý do.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/order-detail/" + orderId;
        }

        boolean isOrderUpdated = orderDetailService.changeStatus(orderId, status, reason);

        if (isOrderUpdated) {
            redirectAttributes.addFlashAttribute("message", "Yêu cầu hoàn trả đã được gửi thành công.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/order-detail/" + orderId;
    }

    @PostMapping("/order-detail/cancel")
    public String cancelOrder(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("reason") String reason,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            logger.warn("Unauthorized cancel attempt. User not authenticated.");
            redirectAttributes.addFlashAttribute("message", "Bạn cần đăng nhập để gửi yêu cầu.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/sign-in";
        }

        if (reason == null || reason.isEmpty()) {
            logger.warn("Invalid return attempt. No reason provided.");
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn lý do.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/order-detail/" + orderId;
        }

        boolean isOrderUpdated = orderDetailService.cancelOrder(orderId, "Cancelled", reason);

        if (isOrderUpdated) {
            logger.info("Return processed successfully for order ID: {}", orderId);
            redirectAttributes.addFlashAttribute("message", "Hủy đơn hàng thành công, tiền đã được hoàn trả vào tài khoản của bạn.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            logger.error("Failed to process return for order ID: {}", orderId);
            redirectAttributes.addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/order-detail/" + orderId;
    }
}
