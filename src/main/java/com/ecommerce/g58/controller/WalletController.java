package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wallet;
import com.ecommerce.g58.repository.WalletRepository;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public String getWallet(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/sign-in";
        }
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Integer userId = user.getUserId();
        List<WalletDTO> wallet = walletService.getTransactionsForUserId(userId);
        if(wallet.isEmpty()) {
            model.addAttribute("message", "Không có giao dịch nào");
        }else {
            WalletDTO firstTransaction = wallet.get(0);
            model.addAttribute("balance", firstTransaction.getWalletBalance());
            model.addAttribute("userId", userId);
        }
        model.addAttribute("wallets", wallet);
        return "wallet";
    }
}
