package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.service.ShippingUnitService;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    private final UserService userService;
    private final StoreService storeService;
    private final ShippingUnitService shippingUnitService;

    @Autowired
    public AdminDashboardController(UserService userService, StoreService storeService, ShippingUnitService shippingUnitService) {
        this.userService = userService;
        this.storeService = storeService;
        this.shippingUnitService = shippingUnitService;
    }

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalSellers", userService.getTotalSellers());
        model.addAttribute("totalStores", storeService.getTotalStores());
        model.addAttribute("totalShippingUnits", shippingUnitService.getTotalShippingUnits());
        return "admin/dashboard";
    }
}
