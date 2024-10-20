package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrdersDTO;
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

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private UserService userService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/my-orders")
    public String getAllOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        // Fetch the authenticated user's email and user data
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();

        List<OrdersDTO> orders = orderService.getOrderSummariesByUserId(userId);  // Fetching the order details from the service
        if (orders.isEmpty()) {
            model.addAttribute("message", "Bạn không có đơn hàng nào.");  // Message if no orders
            model.addAttribute("productListLink", "/product-list");  // Link to the product list
        } else {
            model.addAttribute("orders", orders);  // Passing the list of orders to the model
        }
        return "order";  // Returning the view for the orders page (order.html)
    }
}
