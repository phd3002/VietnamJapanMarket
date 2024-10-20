package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.implementation.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/my-account")
    public String myAccount(Model model) {
        var user = profileService.getUserById(1);
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));
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
                              @RequestParam String newPassword) {
        profileService.updateUser(1, firstName, lastName, email, phoneNumber, password, newPassword);
    }
}
