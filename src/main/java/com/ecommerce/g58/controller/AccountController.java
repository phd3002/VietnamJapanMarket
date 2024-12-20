package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.implementation.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final ProfileService profileService;
    private final StoreService storeService;

    @GetMapping("/my-account")
    public String myAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in"; // Use redirect to avoid returning a relative path
        }

        var email = userDetails.getUsername();
        var user = profileService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        // Pass the user object directly instead of user.getUserId()
        boolean hasStore = storeService.findByOwnerId(user).isPresent();
        model.addAttribute("hasStore", hasStore);

        return "my-account";
    }

    @GetMapping("/my-account/post")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void myAccountPost(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String phoneNumber,
                              @RequestParam String password,
                              @RequestParam String newPassword,
                              @AuthenticationPrincipal UserDetails userDetails) {
        var loggedInEmail = userDetails.getUsername();
        var user = profileService.getUserByEmail(loggedInEmail);
        profileService.updateUser(user.getUserId(), firstName, lastName, email, phoneNumber, password, newPassword);
    }
}
