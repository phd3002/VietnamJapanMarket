package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import com.ecommerce.g58.service.implementation.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final ProfileService profileService;

    @Autowired
    public WalletController(WalletService walletService, UserService userService, ProfileService profileService) {
        this.walletService = walletService;
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping
    public String showWalletPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the user is authenticated
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in";
        }

        // Get user email and retrieve user details
        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);

        if (user == null) {
            return "redirect:/sign-in"; // Redirect if the user is not found
        }

        // Format creation date and add user info to the model
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        // Get wallet balance using the WalletService
        long walletBalance = walletService.getUserWalletBalance(user.getUserId());
        model.addAttribute("balance", walletBalance);

        // Retrieve and add wallet transactions to the model
        List<WalletDTO> walletTransactions = walletService.getTransactionsForUserId(user.getUserId());
        if (walletTransactions.isEmpty()) {
            model.addAttribute("message", "Không có giao dịch nào");
        }

        model.addAttribute("wallets", walletTransactions);
        model.addAttribute("userId", user.getUserId());

        return "wallet"; // Return the view name for wallet.html
    }
}
