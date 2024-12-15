package com.ecommerce.g58.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // This will be called for any error, including 403, 404, 500, etc.
        return "/error/404";  // Or any custom page, e.g., a generic error page
    }

    @Override
    public String getErrorPath() {
        return "/error"; // Spring Boot looks for this method to know where to redirect errors
    }
}
