package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ecommerce.g58.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Map<String, Users> temporaryUsers = new HashMap<>(); // Temporary storage for OTP

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private WalletService walletService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registration form page
    @GetMapping("/sign-up")
    public String showRegistrationForm(Model model) {
        Users user = new Users();
        model.addAttribute("users", user);
        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String registerUser(@ModelAttribute("users") Users users,
                               @RequestParam("confirmPassword") String confirmPassword,  // Confirm password from form
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) throws Exception {

        try {
            // Validate fields...
            if (users.getUsername() == null || users.getUsername().isEmpty()) {
                model.addAttribute("errorMessage", "Tên đăng nhập không được để trống.");
                return "sign-up";
            }

            if (userService.isUsernameExist(users.getUsername())) {
                model.addAttribute("errorMessage", "Tên người dùng đã được sử dụng.");
                return "sign-up";
            }

            if (users.getEmail() == null || users.getEmail().isEmpty()) {
                model.addAttribute("errorMessage", "Email không được để trống.");
                return "sign-up";
            }

            if (users.getPassword() == null || users.getPassword().isEmpty()) {
                model.addAttribute("errorMessage", "Mật khẩu không được để trống.");
                return "sign-up";
            }

            if (users.getPassword().length() < 6) {
                model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
                return "sign-up";
            }

            if (!users.getPassword().equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
                return "sign-up";
            }

            if (userService.isEmailExist(users.getEmail())) {
                model.addAttribute("errorMessage", "Email đã được sử dụng.");
                return "sign-up";
            }

            // Generate and send OTP
            String otp = generateOTP(6); // Generate OTP
            temporaryUsers.put(otp, users); // Store user and OTP temporarily

            sendOTPByEmail(users.getEmail(), otp); // Send OTP via email
            request.getSession().setAttribute("otp", otp); // Store OTP in session
            users.setCreatedAt(LocalDateTime.now()); // Set creation time
            return "redirect:/sign-up/confirm-code"; // Redirect to OTP confirmation page

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại");
            return "redirect:/sign-up"; // Redirect back to sign-up on error
        }
    }

    public String generateOTP(int length) {
        Random random = new Random(); // Create Random object
        StringBuilder otp = new StringBuilder(); // StringBuilder for OTP

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Append a random digit
        }
        return otp.toString(); // Return OTP as string
    }

    @GetMapping("/sign-up/confirm-code") // Handle GET request
    public String showVerifyOtpForm() {
        return "confirm-code";
    }

    @PostMapping("/sign-up/confirm-code") // Handle POST request to confirm OTP
    public String processVerifyOtp(HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) throws Exception {
        String otpFromSession = (String) session.getAttribute("otp"); // Retrieve OTP from session
        if (otpFromSession != null && otpFromSession.equals(request.getParameter("otp"))) { // Validate OTP
            Users user = temporaryUsers.get(otpFromSession); // Get user from temporary storage
            user.setStatus("active"); // Set user status to active
            userService.registerUser(user); // Register the user
            walletService.createWalletForUser(user, 0); // Create wallet for the user
            temporaryUsers.remove(otpFromSession); // Remove user from temporary storage
            session.setAttribute("verificationSuccessMessage", "Xác minh OTP thành công!"); // Set success message
            session.removeAttribute("otp"); // Remove OTP from session
            return "redirect:/sign-in"; // Redirect to sign-in page
        } else {
            redirectAttributes.addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!"); // Set error message
            return "redirect:/sign-up/confirm-code"; // Redirect back to OTP confirmation
        }
    }

    public void sendOTPByEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("G58@gmail.com", "G58 Support");
            helper.setTo(email);

            String subject = "OTP for Registration";

            String content = "<p>Xin chào,</p>"
                    + "<p>OTP của bạn để đăng ký là: <strong>" + otp + "</strong></p>"
                    + "<p>Vui lòng sử dụng OTP này để hoàn tất đăng ký của bạn.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);  // Send email

        } catch (Exception e) {
            logger.error("Error sending OTP email", e);
            throw new RuntimeException("Không thể gửi OTP qua email");
        }
    }

    // Login form page
    @GetMapping("/sign-in")
    public String showLoginForm(Authentication authentication, Model model, HttpSession session) {
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            // Log user authorities
            authentication.getAuthorities().forEach(auth ->
                    logger.debug("User '{}' has authority: {}", authentication.getName(), auth.getAuthority()));

            // Redirect based on roles
            if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"))) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/homepage";
        } else {
            if (session.getAttribute("verificationSuccessMessage") != null) {
                model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage"));
                session.removeAttribute("verificationSuccessMessage");
            }
            return "sign-in";
        }
    }

    @PostMapping("/sign-in")
    public String loginSubmit(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              Model model) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (userDetails == null) {
                model.addAttribute("errorMessage", "Email chưa được đăng kí");
                return "sign-in";
            }
            if (!userService.isAccountActive(email)) {
                model.addAttribute("errorMessage", "Tài khoản của bạn đã bị khóa.");
                return "sign-in";
            }
            // Check if the password matches
            boolean isPasswordValid = userService.checkPassword(password, userDetails.getPassword());
            if (!isPasswordValid) {
                model.addAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu.");
                return "sign-in";
            }

            // Authenticate the user
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("User '{}' authenticated with authorities: {}", email, userDetails.getAuthorities());

            // Redirect based on user role
            if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_Logistic"))) {
                return "redirect:/logistic/order-manager";
            } else if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_Admin"))) {
                return "redirect:/admin/dashboard";
            } else if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_Seller"))) {
                return "redirect:/seller/dashboard";
            }

            return "redirect:/homepage"; // Redirect to homepage after successful login

        } catch (BadCredentialsException e) {
            logger.debug("BadCredentialsException: {}", e.getMessage());
            model.addAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu.");
            return "sign-in";
        } catch (Exception e) {
            logger.debug("Exception during login: {}", e.getMessage());
            model.addAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu.");
            return "sign-in";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, Model model) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        model.addAttribute("successMessage", "Đăng xuất thành công.");
        return "redirect:/sign-in?logout"; // Redirect to login page after logout
    }


    //dẫn đến đường link forgot password
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    //thực hiện quá trình reset
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
        try {
            if (!userService.isAccountActive(email)) {
                model.addAttribute("errorMessage", "Tài khoản của bạn đã bị khóa và không thể thực hiện yêu cầu đặt lại mật khẩu.");
                return "forgot-password";
            }
            userService.sendResetPasswordEmail(email, request);
            redirectAttributes.addFlashAttribute("successMessage", "Chúng tôi đã gửi Email về cho bạn");
        } catch (BadCredentialsException e) {
            model.addAttribute("errorMessage", "Chúng tôi không thấy có Email người dùng ");
            return "forgot-password";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Chúng tôi không thấy có Email người dùng");
            return "forgot-password";
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!userService.isResetTokenValid(token)) {
            model.addAttribute("errorMessage", "Mã thông báo đặt lại không hợp lệ hoặc đã hết hạn.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Mật Khẩu phải dài ít nhất 6 ký tự.");
            return "/reset-password";
        }
        // Check if the password and confirm password do not match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật Khẩu và Xác Nhận Mật Khẩu không khớp.");
            return "/reset-password";
        }
        try {
            userService.updatePasswordReset(token, password);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại Mật Khẩu thành công.");
            return "redirect:/sign-in";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt lại Mật Khẩu thất bại.");
            return "redirect:/reset-password?token=" + token;
        }
    }
}
