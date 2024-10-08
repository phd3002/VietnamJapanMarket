package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.Random;

@Controller
public class UserController {
    private Map<String, Users> temporaryUsers = new HashMap<>(); // Khai báo một bản đồ tạm thời để lưu trữ người dùng và OTP
    @Autowired
    private UserService userService;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender mailSender;

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
                               @RequestParam("confirmPassword") String confirmPassword,  // Nhận mật khẩu xác nhận từ form
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) throws Exception {

        try {
            // Kiểm tra xem mật khẩu và confirmPassword có khớp không
            if (!users.getPassword().equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
                return "sign-up";
            }

            // Kiểm tra email đã tồn tại chưa
            if (userService.isEmailExist(users.getEmail())) {
                model.addAttribute("errorMessage", "Email đã tồn tại.");
                return "sign-up";
            }

            // Đăng ký người dùng mới
//            userService.registerUser(users);
//            model.addAttribute("successMessage", "Đăng ký thành công!");

            // Tạo OTP và lưu vào session
            String otp = generateOTP(6); // Tạo mã OTP
            temporaryUsers.put(otp, users); // Lưu người dùng và OTP vào bản đồ tạm thời

            sendOTPByEmail(users.getEmail(), otp); // Gửi OTP qua email
            request.getSession().setAttribute("otp", otp); // Lưu OTP vào session
            users.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian tạo cho người dùng
            return "redirect:/sign-up/confirm-code"; // Chuyển hướng đến trang xác nhận OTP

        } catch (Exception e) {
            e.printStackTrace(); // Thêm dòng in ra lỗi để kiểm tra
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
            return "redirect:/sign-up"; // Chuyển hướng lại trang đăng ký nếu lỗi
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

    @PostMapping("/sign-up/confirm-code") // Xử lý yêu cầu POST tới /sign-up/verify
    public String processVerifyOtp(HttpServletRequest request,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) throws Exception {
        String otpFromSession = (String) session.getAttribute("otp"); // Lấy OTP từ session
        if (otpFromSession != null && otpFromSession.equals(request.getParameter("otp"))) { // Kiểm tra xem OTP có khớp không
            Users user = temporaryUsers.get(otpFromSession); // Lấy người dùng từ bản đồ tạm thời
            userService.registerUser(user); // Đăng ký người dùng mới
            temporaryUsers.remove(otpFromSession); // Xóa người dùng khỏi bản đồ tạm thời
            session.setAttribute("verificationSuccessMessage", "Xác minh OTP thành công!"); // Thêm thông báo thành công vào session
            session.removeAttribute("otp"); // Xóa OTP khỏi session
            return "/sign-in"; // Chuyển hướng đến trang đăng nhập
        } else {
            redirectAttributes.addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!"); // Thêm thông báo lỗi OTP vào redirectAttributes
            return "redirect:/sign-up/confirm-code"; // Chuyển hướng lại trang xác minh OTP
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

    // Login form page
    @GetMapping("/sign-in")
    public String showLoginForm(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/homepage";
        } else {
            return "sign-in";
        }
//        model.addAttribute("users", new Users());

    }

    @PostMapping("/sign-in")
    public String loginSubmit(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              Model model) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(email);
            // Tạo đối tượng Authentication token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password,userDetails.getAuthorities());
            System.out.println(email);
            System.out.println(password);
            // Xác thực người dùng bằng AuthenticationManager
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Dang nhap duoc roi cmm");
            return "redirect:/homepage"; // Chuyển hướng đến trang chủ sau khi đăng nhập thành công

        } catch (BadCredentialsException e) {
            model.addAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu.");
            return "sign-in";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi. Vui lòng thử lại.");
            return "sign-in";
        }
    }
}
