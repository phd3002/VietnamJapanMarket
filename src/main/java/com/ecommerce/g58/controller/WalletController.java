package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WalletService;
import com.ecommerce.g58.service.implementation.ProfileService;
import com.ecommerce.g58.utils.FormatVND;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    // Display the wallet page with pagination and date filters
    public String showWalletPage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        // Check if user is authenticated
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in";
        }

        // Retrieve user information based on email
        String email = userDetails.getUsername();
        Users user = profileService.getUserByEmail(email);

        if (user == null) {
            return "redirect:/sign-in";
        }

        // Add user information to the model
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        // Retrieve wallet balance and add to the model
        long walletBalance = walletService.getUserWalletBalance(user.getUserId());
        model.addAttribute("balance", FormatVND.formatCurrency(BigDecimal.valueOf(walletBalance)));

        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        // Fetch paginated and filtered transactions
        Page<WalletDTO> walletPage = walletService.getTransactionsForUserId(
                user.getUserId(),
                startDateTime,
                endDateTime,
                page,
                size
        );

        // Add transactions and pagination info to the model
        model.addAttribute("wallets", walletPage.getContent());
        model.addAttribute("currentPage", walletPage.getNumber());
        model.addAttribute("totalPages", walletPage.getTotalPages());
        model.addAttribute("totalItems", walletPage.getTotalElements());
        model.addAttribute("startDate", (startDate != null) ? startDate.toString() : "");
        model.addAttribute("endDate", (endDate != null) ? endDate.toString() : "");

        // Add a message if no transactions are found
        if (walletPage.isEmpty()) {
            model.addAttribute("message", "Không có giao dịch.");
        }

        return "wallet"; // Thymeleaf template name
    }
}
