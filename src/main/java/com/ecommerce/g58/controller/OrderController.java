package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
//    private final UserService userService;

//    public OrderController(OrderService orderService, UserService userService) {
//        this.orderService = orderService;
//        this.userService = userService;
//    }

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/my-orders")
    public String getAllOrders(Model model) {
        List<OrdersDTO> orders = orderService.getOrderSummaries();  // Fetching the order details from the service
        model.addAttribute("orders", orders);  // Passing the list of orders to the model
        return "order";  // Returning the view for the orders page (order.html)
    }

//    @GetMapping("/order/my-orders")
//    public String getAllOrders(Model model, Principal principal) {
//        String email = principal.getName();
//        Users user = userService.findByEmail(email);
//        Integer userId = user.getUserId();
//        List<OrdersDTO> orders = orderService.getOrderSummaries(userId);
//        model.addAttribute("orders", orders);
//        return "order";  // This should map to your Thymeleaf template (order.html)
//    }
}
