package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class ResetPasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;




    @GetMapping("/change-password")
    public String showResetPasswordPage() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleResetPassword(Principal principal,
                                      @RequestParam("oldPassword") String oldPassword,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      HttpServletRequest request,
                                      Model model) {

        Users user = userRepository
                .findByEmail(principal.getName());
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu cũ không đúng");
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu không khớp");
            return "change-password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải chứa ít nhất 6 ký tự");
            return "change-password";
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword()) == passwordEncoder.matches(newPassword, user.getPassword())) {
            model.addAttribute("error", "Mật khẩu mới không được trùng với mật khẩu cũ");
            return "change-password";
        }


        if (user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng với email = " + principal.getName());
            return "change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updatePassword(user, newPassword);
        model.addAttribute("successMessage", "Cập nhật mật khẩu thành công");
        return "/change-password";
    }
}