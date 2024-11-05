package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserToModel(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName(); // Get the logged-in user's email
            Users user = userService.findByEmail(email);

            if (user != null) {
                model.addAttribute("firstname", user.getFirstName());  // Pass the first name
                model.addAttribute("username", user.getUsername());  // Pass the username
                model.addAttribute("isLoggedIn", true);  // Pass logged-in status
            } else {
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);  // If not logged in
        }
    }
}