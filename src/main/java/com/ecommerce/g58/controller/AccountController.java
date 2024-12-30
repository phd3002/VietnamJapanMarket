package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.implementation.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final ProfileService profileService;
    private final StoreService storeService;
    private final UserService userService;

    @GetMapping("/my-account")
    public String myAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in"; // Use redirect to avoid returning a relative path
        }
        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "my-account";
    }

    @PostMapping("/update-account")
    public String updateAccount(@RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String district,
                                @RequestParam(required = false) String ward,
                                @RequestParam(required = false) String address,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in";
        }

        // Validate input parameters
        if (firstName == null || firstName.isEmpty() || firstName.length() > 100) {
            model.addAttribute("error", "First name cannot be empty and must not exceed 100 characters.");
            return "redirect:my-account";
        }
        if (lastName == null || lastName.isEmpty() || lastName.length() > 100) {
            model.addAttribute("error", "Last name cannot be empty and must not exceed 100 characters.");
            return "redirect:my-account";
        }
        if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() > 20 || !phoneNumber.matches("^[0-9]*$") || phoneNumber.length() < 10) {
            model.addAttribute("error", "Phone number cannot be empty, must be between 10 and 20 characters, and must contain only digits.");
            return "redirect:my-account";
        }
        if (address == null || address.isEmpty() || address.length() > 255 || address.contains("-")) {
            model.addAttribute("error", "Address cannot be empty, must not exceed 255 characters, and must not contain hyphens.");
            return "redirect:my-account";
        }

        // Update user details
        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);
        String detailAddress = ward + "-" + district + "-" + city + "-" + address.trim();
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setCity(city);
            user.setDistrict(district);
            user.setWard(ward);
            user.setAddress(detailAddress);
        }
        userService.saveUser(user);
        model.addAttribute("message", "Cập nhật thông tin thành công!");
        return "redirect:my-account";
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
