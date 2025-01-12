    package com.ecommerce.g58.controller.Seller;

    import com.ecommerce.g58.dto.WalletDTO;
    import com.ecommerce.g58.entity.Users;
    import com.ecommerce.g58.entity.Wallet;
    import com.ecommerce.g58.repository.WalletRepository;
    import com.ecommerce.g58.service.OtpService;
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
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.io.IOException;
    import java.math.BigDecimal;
    import java.security.Principal;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Optional;

    @Controller
    @RequestMapping("/seller/wallet")
    public class SellerWalletController {
        private final WalletService walletService;
        private final ProfileService profileService;

        @Autowired
        private OtpService otpService;

        @Autowired
        WalletRepository walletRepository;

        @Autowired
        public SellerWalletController(WalletService walletService, ProfileService profileService) {
            this.walletService = walletService;
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

            return "seller/wallet"; // Thymeleaf template name
        }

        @PostMapping("/withdraw")
        public String withdraw(@RequestParam String bankName,
                               @RequestParam String accountNumber,
                               @RequestParam BigDecimal withdrawalAmount,
                               RedirectAttributes redirectAttributes, Principal principal) {

            // 1. Check bankName validity
            if (bankName == null || bankName.isBlank() || bankName.length() > 20) {
                redirectAttributes.addFlashAttribute("error",
                        "Tên ngân hàng không được để trống và không được vượt quá 20 ký tự.");
                return "redirect:/seller/wallet";
            }
            // Ensure bankName has only letters
            if (!bankName.matches("^[a-zA-Z]+$")) {
                redirectAttributes.addFlashAttribute("error",
                        "Tên ngân hàng chỉ được chứa chữ cái (A–Z, a–z).");
                return "redirect:/seller/wallet";
            }

            // 2. Check accountNumber validity
            if (accountNumber == null || accountNumber.isBlank()) {
                redirectAttributes.addFlashAttribute("error",
                        "Số tài khoản không được để trống.");
                return "redirect:/seller/wallet";
            }
            // Ensure accountNumber has only digits
            if (!accountNumber.matches("^[0-9]+$")) {
                redirectAttributes.addFlashAttribute("error",
                        "Số tài khoản chỉ được chứa chữ số (0–9).");
                return "redirect:/seller/wallet";
            }

            if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Số tiền rút phải lớn hơn 0.");
                return "redirect:/seller/wallet";
            }
            if (principal == null) {
                redirectAttributes.addFlashAttribute("error",
                        "Xin vui lòng đăng nhập để thực hiện");
                return "redirect:/seller/wallet";
            }
            Users user = profileService.getUserByEmail(principal.getName());
            Optional<Wallet> wallet = walletRepository.findByUserId(user);
            if (wallet.isPresent()) {
                if (wallet.get().getBalance() < withdrawalAmount.doubleValue()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Số dư không đủ, bạn chỉ có thể rút tối đa " + wallet.get().getBalance());
                    return "redirect:/seller/wallet";
                }
            }
            if (wallet.isPresent()) {
                walletService.withdraw(wallet.get(), bankName, accountNumber, withdrawalAmount);
                redirectAttributes.addFlashAttribute("success",
                        "Yêu cầu rút tiền thành công");
            }
            return "redirect:/seller/wallet";
        }
        @PostMapping("/send-otp")
        @ResponseBody
        public Map<String, Object> sendOtp(@RequestParam String bankName,
                                           @RequestParam String accountNumber,
                                           @RequestParam BigDecimal withdrawalAmount,
                                           Principal principal) {
            Map<String, Object> response = new HashMap<>();

            try {
                if (principal == null) {
                    throw new RuntimeException("Chưa đăng nhập!");
                }
                Users user = profileService.getUserByEmail(principal.getName());
                if (user == null) {
                    throw new RuntimeException("Không tìm thấy user!");
                }

                // Validate sơ bộ
                if (bankName.isBlank() || accountNumber.isBlank() || withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Thông tin rút tiền không hợp lệ!");
                }

                // Gọi service sinh OTP, gửi email
                otpService.generateAndSendOtp(user);

                response.put("status", "OK");
            } catch (Exception e) {
                response.put("status", "FAIL");
                response.put("message", e.getMessage());
            }
            return response;
        }

        // ========== 3) Verify OTP ==========
        @PostMapping("/verify-otp")
        @ResponseBody
        public Map<String, Object> verifyOtp(@RequestParam String otp,
                                             @RequestParam String bankName,
                                             @RequestParam String accountNumber,
                                             @RequestParam BigDecimal withdrawalAmount,
                                             Principal principal) {
            Map<String, Object> response = new HashMap<>();

            try {
                if (principal == null) {
                    throw new RuntimeException("Chưa đăng nhập!");
                }
                Users user = profileService.getUserByEmail(principal.getName());
                if (user == null) {
                    throw new RuntimeException("Không tìm thấy user!");
                }

                // Gọi service check OTP
                boolean isValid = otpService.verifyOtp(user, otp);
                if (!isValid) {
                    throw new RuntimeException("OTP không đúng hoặc đã hết hạn!");
                }

                response.put("status", "OK");
            } catch (Exception e) {
                response.put("status", "FAIL");
                response.put("message", e.getMessage());
            }
            return response;
        }
    }
