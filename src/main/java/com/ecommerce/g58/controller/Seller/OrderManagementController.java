package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderManagementController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/seller/order-manager")
    public String listOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();
        List<OrderManagerDTO> orders = orderService.getOrdersForStore(userId);
        model.addAttribute("orderss", orders);
        return "seller/order-manager";
    }

    @GetMapping("/seller/order-manager/{storeId}")
    public String getOrderManagementPage(@PathVariable("storeId") Integer storeId, Model model, Principal principal) {
        List<OrderManagerDTO> orders = orderService.getOrdersByStoreId(storeId);
        model.addAttribute("orders", orders);
        return "seller/order-manager2";
    }

    @PostMapping("/seller/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId, @RequestParam("status") String status, HttpServletRequest request) {
        orderService.updateOrderStatuss(orderId, status);
        System.out.println("Order status updated to " + status);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }

}
