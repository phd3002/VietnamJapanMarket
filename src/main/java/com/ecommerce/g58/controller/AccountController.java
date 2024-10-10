package com.ecommerce.g58.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/my-account")
    public String myAccount(Model model) {
        return "my-account";
    }
}
