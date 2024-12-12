package com.ecommerce.g58.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsOfServiceController {

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }
}