package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.OrderDetailService;
import com.ecommerce.g58.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderManagementController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;


    @GetMapping("/seller/order-manager")
    public String getOrderManagementPage(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Model model,
            Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Users owner = userRepository.findByEmail(authentication.getName());
        Optional<Stores> storeOwner = storeRepository.findByOwnerId(owner);

        if (!storeOwner.isPresent()) {
            model.addAttribute("error", "Không tìm thấy cửa hàng của bạn.");
            return "seller/order-manager2";
        }

        Integer storeId = storeOwner.get().getStoreId();

        // Parse dates
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ.");
        }

        List<OrderManagerDTO> orders;

        if ((status == null || status.isEmpty()) && startDate == null && endDate == null) {
            // No filters applied
            orders = orderService.getOrdersByStoreId(storeId);
        } else {
            // Filters applied
            orders = orderService.getOrdersByFilters(status, startDate, endDate, storeId);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("storeId", storeId);
        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("startDate", startDateStr != null ? startDateStr : "");
        model.addAttribute("endDate", endDateStr != null ? endDateStr : "");

        return "seller/order-manager2";
    }


    @PostMapping("/seller/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId, @RequestParam("status") String status, HttpServletRequest request) {
        orderService.updateOrderStatuss(orderId, status);
        System.out.println("Order status updated to " + status);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }

    @PostMapping("/seller//return")
    public String returnOrder(
            @RequestParam("orderId") Integer orderId,
            Integer storeId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "Bạn cần đăng nhập để gửi yêu cầu.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/sign-in";
        }

        boolean isOrderUpdated = orderDetailService.refundOrder(orderId);
        if (isOrderUpdated) {
            redirectAttributes.addFlashAttribute("message", "Hoàn trả đơn hàng thành công.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:order-manager/";
    }
    @PostMapping("/seller/bulk-update-status")
    public String bulkUpdateOrderStatus(
            @RequestParam("statusFilter") String statusFilter,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes redirectAttributes,
            Principal principal
    ) {
        // Authenticate the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Users owner = userRepository.findByEmail(authentication.getName());
        Optional<Stores> storeOwner = storeRepository.findByOwnerId(owner);

        if (!storeOwner.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy cửa hàng của bạn.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/seller/order-manager";
        }

        Integer storeId = storeOwner.get().getStoreId();

        // Parse dates
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Định dạng ngày không hợp lệ.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/seller/order-manager";
        }

        try {
            // Perform the bulk update
            orderService.bulkUpdateOrderStatus(statusFilter, newStatus, startDate, endDate, storeId);
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái các đơn hàng thành công.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        // Redirect back to order manager with current filter
        String redirectUrl = "/seller/order-manager?status=" + (statusFilter != null ? statusFilter : "") +
                "&startDate=" + (startDateStr != null ? startDateStr : "") +
                "&endDate=" + (endDateStr != null ? endDateStr : "");
        return "redirect:" + redirectUrl;
    }

}
