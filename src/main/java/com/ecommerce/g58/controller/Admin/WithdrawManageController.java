package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.TransactionRepository;
import com.ecommerce.g58.service.EmailService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/withdraw-manage")
public class WithdrawManageController {
    private final WalletService walletService;
    private final ProfileService profileService;

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Autowired
    public WithdrawManageController(WalletService walletService,
                                    ProfileService profileService,
                                    TransactionRepository transactionRepository,
                                    EmailService emailService
    ) {
        this.walletService = walletService;
        this.profileService = profileService;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @GetMapping
    // Display the wallet page with pagination and date filters
    public String showWalletPage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
        Page<WalletDTO> walletPage = walletService.getWithdrawRequest(
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

        return "admin/withdraw-manage";
    }

    @PostMapping("/confirm")
    public String confirmWithdraw(@RequestParam("transactionId") Integer transactionId,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1) Tìm transaction theo id
            Transactions tx = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch ID=" + transactionId));

            // 2) Chỉ cho confirm nếu status = 2 (đang xử lý)
            if (!"2".equals(tx.getStatus())) {
                throw new RuntimeException("Giao dịch không ở trạng thái chờ xử lý!");
            }

            // 3) Update status -> "1" (hoàn thành)
            tx.setStatus("1");
            transactionRepository.save(tx);

            // 4) Gửi email
            Users user = tx.getToWalletId().getUserId(); // tuỳ logic: có thể fromWalletId
            emailService.sendEmailAsync(user.getEmail(),
                    "[VJ-Market] Giao dịch rút tiền đã được xác nhận",
                    "<p> Yêu cầu rút tiền  của bạn đã được xác nhận thành công!</p>"
            );

            // 5) Dùng FlashAttribute để show message
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận rút tiền thành công!");

        } catch (Exception e) {
            // 6) Nếu lỗi thì addFlashAttribute errorMessage
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác nhận: " + e.getMessage());
        }
        // Quay về trang /admin/withdraw-manage
        return "redirect:/admin/withdraw-manage";
    }

    @PostMapping("/deny")
    public String denyWithdraw(@RequestParam("transactionId") Integer transactionId,
                               @RequestParam("reason") String reason,
                               RedirectAttributes redirectAttributes) {
        try {
            Transactions tx = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch ID=" + transactionId));

            if (!"2".equals(tx.getStatus())) {
                throw new RuntimeException("Giao dịch không ở trạng thái chờ xử lý!");
            }

            // Đổi status -> 3 (Từ chối)
            tx.setStatus("3");

            // Ghi thêm lý do vào description
            String oldDesc = tx.getDescription() != null ? tx.getDescription() : "";
            tx.setDescription(oldDesc + "<br><strong>Lý do từ chối:</strong> " + reason);

            transactionRepository.save(tx);

            // Gửi email kèm lý do
            Users user = tx.getToWalletId().getUserId();
            String subject = "[VJ-Market] Giao dịch rút tiền đã bị từ chối";
            String body = "<p>Giao dịch rút tiền của bạn đã bị từ chối.</p>"
                    + "<p>Lý do: <strong>" + reason + "</strong></p>";

            emailService.sendEmailAsync(user.getEmail(), subject, body);

            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối yêu cầu rút tiền!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi từ chối: " + e.getMessage());
        }
        return "redirect:/admin/withdraw-manage";
    }
}
