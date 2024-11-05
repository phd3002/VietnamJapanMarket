package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import com.ecommerce.g58.service.implementation.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public String myAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in"; // Use redirect to avoid returning a relative path
        }

        var email = userDetails.getUsername();
        var user = profileService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));
        List<WalletDTO> wallet = walletService.getTransactionsForUserId(user.getUserId());
        if(wallet.isEmpty()) {
            model.addAttribute("message", "Không có giao dịch nào");
        }else {
            WalletDTO firstTransaction = wallet.get(0);
            model.addAttribute("balance", firstTransaction.getWalletBalance());
            model.addAttribute("userId", user.getUserId());
        }
        model.addAttribute("wallets", wallet);
        return "wallet";
    }
}
