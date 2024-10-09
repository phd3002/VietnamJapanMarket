package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customer/orders")
    public String getAllOrderDetails(Model model) {
        List<OrdersDTO> orders = orderService.getOrderDetails();
        model.addAttribute("orders", orders); // Truyền dữ liệu vào model
        return "order"; // Trả về view order.html
    }
}
