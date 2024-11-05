package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@Controller
public class OrderController {

    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String getOrders(@RequestParam(value = "status", required = false) String status,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }

        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        boolean hasStore = storeService.findByOwnerId(user).isPresent();
        model.addAttribute("hasStore", hasStore);

        Page<OrdersDTO> orderPage = orderService.getOrdersByUserIdAndStatus(user.getUserId(), status, PageRequest.of(page, size));

        if (orderPage.isEmpty()) {
            model.addAttribute("message", "Không có đơn hàng nào. Hãy mua ngay!");
            model.addAttribute("productListLink", "/product-detail");
        } else {
            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalItems", orderPage.getTotalElements());
            model.addAttribute("status", status);
        }

        return "order";
    }

}