package com.ecommerce.g58.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckoutController {

    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "checkout"; // This should match the file name 'checkout.html' in the templates folder
    }
}
