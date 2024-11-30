package com.ecommerce.g58.controller.Logistic;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderManagementControllerr {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/logistic/order-manager")
    public String getOrderManagementPage(
            @RequestParam(value = "status", required = false) String status,
            Model model,
            Principal principal) {

        List<OrderManagerDTO> orders;
        if (status == null || status.isEmpty()) {
            orders = orderService.getOrders();
        } else {
            orders = orderService.getOrdersByStatus(status);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("status", status); // Pass selected status to the template
        return "logistic/order-manager";
    }


    @PostMapping("/logistic/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId, @RequestParam("status") String status, HttpServletRequest request) {
        orderService.updateOrderStatuss(orderId, status);
//        System.out.println("Order status updated to " + status);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }
}
