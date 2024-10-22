package com.ecommerce.g58.utils;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserService userService;

    public SecurityUtils(UserService userService) {
        this.userService = userService;
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                String email = ((UserDetails) principal).getUsername();

                // Use UserService to get the Users entity by email
                Users user = userService.findByEmail(email);
                return user != null ? user.getUserId() : null;
            }
        }
        return null; // No authenticated user found
    }
}