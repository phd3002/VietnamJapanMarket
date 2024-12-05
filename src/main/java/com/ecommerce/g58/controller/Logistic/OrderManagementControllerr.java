package com.ecommerce.g58.controller.Logistic;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class OrderManagementControllerr {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/logistic/order-manager")
    public String getOrderManagementPage(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model,
            Principal principal) {

        List<OrderManagerDTO> orders;
        if ((status == null || status.isEmpty()) && startDate == null && endDate == null) {
            orders = orderService.getOrders();
        } else {
            orders = orderService.getOrdersByFilters(status, startDate, endDate);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("status", status); // Pass selected status to the template
        model.addAttribute("startDate", startDate != null ? startDate.toString() : "");
        model.addAttribute("endDate", endDate != null ? endDate.toString() : "");

        return "logistic/order-manager";
    }


    @PostMapping("/logistic/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId, @RequestParam("status") String status, HttpServletRequest request) {
        orderService.updateOrderStatuss(orderId, status);
//        System.out.println("Order status updated to " + status);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }

    @PostMapping("/logistic/bulk-update-status")
    public String bulkUpdateOrderStatus(@RequestParam("statusFilter") String statusFilter,
                                        @RequestParam(value = "startDate", required = false) String startDateStr,
                                        @RequestParam(value = "endDate", required = false) String endDateStr,
                                        @RequestParam("newStatus") String newStatus,
                                        RedirectAttributes redirectAttributes,
                                        Principal principal) {
        try {
            // Parse dates
            LocalDate startDate = null;
            LocalDate endDate = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            }

            // Perform the bulk update
            orderService.bulkUpdateOrderStatus(statusFilter, newStatus, startDate, endDate);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái các đơn hàng thành công.");
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng.");
        }

        // Redirect back to order manager with current filter
        String redirectUrl = "/logistic/order-manager?status=" + (statusFilter != null ? statusFilter : "") +
                "&startDate=" + (startDateStr != null ? startDateStr : "") +
                "&endDate=" + (endDateStr != null ? endDateStr : "");
        return "redirect:" + redirectUrl;
    }
}
