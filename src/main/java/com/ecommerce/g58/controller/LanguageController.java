package com.ecommerce.g58.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LanguageController {

    @GetMapping("/change-language")
    public String setLanguage(@RequestParam(value = "lang") String lang, HttpSession session) {
        if (lang != null) {
            session.setAttribute("lang", lang);
        }
        return "redirect:/";
    }
}