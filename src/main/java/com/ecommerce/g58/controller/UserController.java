package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.UsersDTO;
import com.ecommerce.g58.entity.OTP;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.OTPService;
import com.ecommerce.g58.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Controller
public class UserController {
    private final OTPService otpService;
    // Khai báo một bản đồ tạm thời để lưu trữ người dùng và OTP
    /* TODO: tao ban token and OTP */

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JavaMailSender mailSender;

    public UserController(OTPService otpService, UserService userService, AuthenticationManager authenticationManager, JavaMailSender mailSender) {
        this.otpService = otpService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
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
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) throws Exception {

        try {
            // Kiểm tra mật khẩu và confirmPassword
            if (!users.getPassword().equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
                return "sign-up";
            }

            // Kiểm tra email đã tồn tại chưa
            if (userService.isEmailExist(users.getEmail())) {
                model.addAttribute("errorMessage", "Email đã tồn tại.");
                return "sign-up";
            }

            if (userService.isUsernameExist(users.getUsername())) {
                model.addAttribute("errorMessage", "Username đã tồn tại.");
                return "sign-up";
            }

            // Generate OTP
            String otp = generateOTP(6);

            // Temporarily store user and OTP in the database
            userService.saveTemporaryUser(users, otp);  // Save user temporarily
            sendOTPByEmail(users.getEmail(), otp);      // Send OTP via email
            request.getSession().setAttribute("otp", otp);

            return "redirect:/sign-up/confirm-code";

        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi. Vui lòng thử lại.");
            return "redirect:/sign-up";
        }
    }

    public String generateOTP(int length) {
        Random random = new Random(); // Tạo đối tượng Random
        StringBuilder otp = new StringBuilder(); // Tạo StringBuilder để xây dựng OTP

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Thêm một chữ số ngẫu nhiên vào OTP
        }
        return otp.toString(); // Trả về OTP dưới dạng chuỗi
    }

    @GetMapping("/sign-up/confirm-code") // Xử lý yêu cầu GET
    public String showVerifyOtpForm() {
        return "confirm-code";
    }

    @PostMapping("/sign-up/confirm-code")
    public String processVerifyOtp(HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) throws Exception {
        String otpCode = (String) session.getAttribute("otp"); // Get OTP from session
        String enteredOtp = request.getParameter("otp");

        // Check OTP in the database
        OTP otp = otpService.findByOtpCode(otpCode);
        if (otp != null && otp.getOtpCode().equals(enteredOtp) && !otp.isUsed()) {
            // Mark OTP as used
            otp.setUsed(true);
            otpService.saveOTP(otp);

            // Register the user now
            Users user = otp.getUserId();
            user.setStatus("ACTIVE");  // Mark the user as active
            userService.registerUser(user);  // Save the user into the database
            // Chuyển hướng đến trang đăng nhập
            session.setAttribute("verificationSuccessMessage", "Xác minh OTP thành công!");
            return "redirect:/sign-in";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "OTP không hợp lệ. Vui lòng thử lại!");
            return "redirect:/sign-up/confirm-code";
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

            mailSender.send(message);  // Gửi email

        } catch (Exception e) {
            e.printStackTrace();  // Thêm log để kiểm tra lỗi
            throw new RuntimeException("Không thể gửi OTP qua email");
        }
    }

    // Login form pageFuu TOan fixing
//    @GetMapping("/sign-in")
//    public String showLoginForm(Authentication authentication, Model model, HttpSession session) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            return "redirect:/homepage";
//        } else if (session.getAttribute("verificationSuccessMessage") != null) {
//            model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage"));
//            session.removeAttribute("verificationSuccessMessage");
//            model.addAttribute("users", new Users());
//        } else {
//            return "sign-in";
//        }
//        model.addAttribute("user_info", new UsersDTO());
//        return "sign-in";
//    }

//    @PostMapping("/sign-in")
//    public String loginSubmit(@ModelAttribute("user_info") UsersDTO userInfo, Model model, HttpServletRequest request, HttpServletResponse response) {
//        System.out.println("Da vao duoc day chua");
//        try {
//            // Tạo đối tượng Authentication token
//            String currentUsername = userInfo.getUsername();
//            String currentPassword = userInfo.getPassword();
//            System.out.println("Tao o day ne");
//            System.out.println(currentUsername);
//            System.out.println(currentPassword);
////                UserDetails userDetails = userService.loadUserByUsername(currentUsername);
////
////                if (userDetails == null) {
////                    model.addAttribute("error", "Email không tồn tại");
////                    return "sign-in";
////                }
//
//                // Sử dụng AuthenticationManager để xác thực
////            UsernamePasswordAuthenticationToken authentication =
////                    new UsernamePasswordAuthenticationToken(userDetails, currentPassword, userDetails.getAuthorities());
//
////            SecurityContextHolder.getContext().setAuthentication(authentication);
//                Authentication authentication = authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(currentUsername, currentPassword));
//            System.out.println("Tao qua duoc day roi ne ahihi");
//                return "redirect:/homepage"; // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
//
//            } catch (BadCredentialsException e) {
//                model.addAttribute("error", "Sai tài khoản hoặc mật khẩu.");
//                return "sign-in";
//            } catch (Exception e) {
//                model.addAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
//                return "sign-in";
//            }
//        }
//Fuu TOan fixing
//    @GetMapping("/sign-in")
//    public String showLoginForm(Model model, HttpSession session) {
//        // Add the user_info object to the model for Thymeleaf binding
//        model.addAttribute("user_info", new Users());
//
//        // Handle success messages or other session attributes if needed
//        if (session.getAttribute("verificationSuccessMessage") != null) {
//            model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage"));
//            session.removeAttribute("verificationSuccessMessage");
//        }
//
//        return "sign-in";
//    }
//
//    @PostMapping("/sign-in")
//    public String loginSubmit(@ModelAttribute("user_info") Users userInfo, Model model, HttpServletRequest request, HttpServletResponse response) {
//        System.out.println("Da vao duoc day chua");
//        try {
//            // Tạo đối tượng Authentication token
//            String currentUsername = userInfo.getUsername();
//            String currentPassword = userInfo.getPassword();
//            System.out.println("Tao o day ne");
//            System.out.println(currentUsername);
//            System.out.println(currentPassword);
////                UserDetails userDetails = userService.loadUserByUsername(currentUsername);
////
////                if (userDetails == null) {
////                    model.addAttribute("error", "Email không tồn tại");
////                    return "sign-in";
////                }
//
//            // Sử dụng AuthenticationManager để xác thực
////            UsernamePasswordAuthenticationToken authentication =
////                    new UsernamePasswordAuthenticationToken(userDetails, currentPassword, userDetails.getAuthorities());
//
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(currentUsername, currentPassword));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            System.out.println("Tao qua duoc day roi ne ahihi");
//            return "redirect:/homepage"; // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
//
//        } catch (BadCredentialsException e) {
//            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu.");
//            return "sign-in";
//        } catch (Exception e) {
//            model.addAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
//            return "sign-in";
//        }
//    }

    @GetMapping("/sign-in")
    public String loginForm(Authentication authentication,
                            Model model,
                            HttpSession session) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/homepage";
        } else {
//            if (session.getAttribute("verificationSuccessMessage") != null) {
//                model.addAttribute("successMessage", session.getAttribute("verificationSuccessMessage"));
//                session.removeAttribute("verificationSuccessMessage");
//            }
            return "sign-in";
        }
    }


//    @PostMapping("/sign-in")
//    public String loginSubmit(@ModelAttribute("user_info") Users userInfo,
//                              HttpSession session,
//                              Model model) {
//        System.out.println(userInfo.getEmail());
//        System.out.println(userInfo.getPassword());
//        try {
//            UserDetails userDetails = userService.loadUserByUsername(userInfo.getEmail());
//
//            if (userDetails == null) {
//                model.addAttribute("errorMessage", "Email không tồn tại");
//                return "sign-in";
//            }
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, userInfo.getPassword(), userDetails.getAuthorities());
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println(authentication);
//            System.out.println("Dang Nhap duoc roi` cmn");
//            return "redirect:/homepage";
//
//        } catch (BadCredentialsException e) {
//            model.addAttribute("errorMessage", "Sai tài khoản hoặc mật khẩug");
//            return "sign-in";
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Đã xảy ra lỗi. Vui lòng thử lại");
//            return "sign-in";
//        }
//    }


//    @PostMapping("/sign-in")
//    public String loginSubmit(@ModelAttribute("user_info") Users userInfo, Model model, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            String currentUsername = userInfo.getUsername();
//            String currentPassword = userInfo.getPassword();
//            System.out.println(currentPassword);
//            System.out.println(currentUsername);
////            UserDetails userDetails = userService.loadUserByUsername(currentUsername);
////            if (userDetails == null) {
////                System.out.println(currentPassword);
////                System.out.println(currentUsername);
////                model.addAttribute("error", "Invalid username or password.");
////                return "sign-in";
////            }else {
////                Authentication authentication = authenticationManager.authenticate(
////                        new UsernamePasswordAuthenticationToken(currentUsername, currentPassword, userDetails.getAuthorities()));
////                SecurityContextHolder.getContext().setAuthentication(authentication);
////                System.out.println("Dang Nhap duoc roi` cmn");
////                return "redirect:/homepage";
////            }
//
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(currentUsername, currentPassword));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            System.out.println("Dang Nhap duoc roi` cmn");
//            return "redirect:/homepage";
//        } catch (UsernameNotFoundException e) {
//            model.addAttribute("error", "Invalid username or password.");
//            return "sign-in";
//        } catch (Exception e) {
//            model.addAttribute("error", "An error occurred. Please try again.");
//            return "sign-in";
//        }
//    }

    //dẫn đến đường link forgot password
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    //thực hiện quá trình reset
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            userService.sendResetPasswordEmail(email, request);
            redirectAttributes.addFlashAttribute("successMessage", "Chúng tôi đã gửi Email về cho bạn");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chúng tôi không thấy có Email người dùng ");
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
                                       RedirectAttributes redirectAttributes) {
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật Khẩu phải dài ít nhất 6 ký tự.");
            return "redirect:/reset-password?token=" + token;
        }

        try {
            userService.updatePasswordReset(token, password);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại Mật Khẩu thành công.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }

}



