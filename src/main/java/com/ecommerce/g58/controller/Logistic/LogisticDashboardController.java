package com.ecommerce.g58.controller.Logistic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogisticDashboardController {

    @GetMapping("/logistic/dashboard")
    public String showDashboard() {
        return "logistic/dashboard";
    }
}
