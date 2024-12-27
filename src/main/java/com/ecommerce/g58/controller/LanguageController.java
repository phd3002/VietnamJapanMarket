package com.ecommerce.g58.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LanguageController {

    @GetMapping("/change-language")
    public String setLanguage(@RequestParam(value = "lang") String lang,
                              HttpServletRequest request, HttpSession session) {
        if (lang != null) {
            session.setAttribute("lang", lang);
        }

        // Lấy URL trước đó (referer)
        String referer = request.getHeader("Referer");

        // Trả về redirect tới URL trước đó
        return "redirect:" + (referer != null ? referer : "/");
    }
}