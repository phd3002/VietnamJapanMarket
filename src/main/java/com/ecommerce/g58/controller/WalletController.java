package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.WalletService;
import com.ecommerce.g58.service.implementation.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;

@Controller
public class WalletController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private WalletService walletService;

    @GetMapping("/wallet")
    public String getWalletBalance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in";
        }

        // Lấy thông tin người dùng từ UserDetails
        var email = userDetails.getUsername();
        var user = profileService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        // Lấy số dư ví
        Double balance = walletService.getBalanceByUsername(user.getUsername());
        if (balance == null) {
            balance = 0.0;
        }
        model.addAttribute("balance", balance);

        return "wallet";
    }
}