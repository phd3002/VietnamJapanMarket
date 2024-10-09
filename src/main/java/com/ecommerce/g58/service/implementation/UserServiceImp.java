package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ResetToken;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.ResetTokenRepository;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImp implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

//    @Autowired
//    private org.springframework.security.authentication.AuthenticationManager authenticationManager;


    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Users getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public void registerUser(Users users) {
        // Mã hóa mật khẩu
        users.setUsername(users.getUsername());
        users.setEmail(users.getEmail());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setCreatedAt(LocalDateTime.now());

        // Gán role mặc định
        Roles role = roleRepository.findByRoleId(3);// Lấy role người dùng mặc định
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        } else {
            users.setRoleId(role);
        }
        userRepository.save(users);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRoleId().getRoleName())));
    }

    // New method to check if the password matches
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void sendResetPasswordEmail(String email, HttpServletRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(token, user);
        resetTokenRepository.save(resetToken);


        String baseUrl = UrlUtils.getBaseUrl(request);

        // Tạo URL đặt lại mật khẩu
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Đặt lại mật khẩu");

            String htmlMsg = "<html>" +
                    "<body>" +
                    "<p>Xác Nhận Đặt Lại Mật Khẩu, Nhấn vào nút bên dưới:</p>" +
                    "<a href=\"" + resetUrl + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 4px;\">Xác Nhận Đổi Mật Khẩu</a>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    //check token xem đã hết hạn hoặc là check token có tồn tại không
    @Override
    public boolean isResetTokenValid(String token) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }


    @Override
    //truyền vào string token và password sẽ nhập vào
    public void updatePasswordReset(String token, String password) {
        //get thông tin của token đó
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        Users user = resetToken.getUser();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }

    @Override
    public void updatePassword(Users user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

//    public Authentication authenticate(String email, String password) throws AuthenticationException {
//        try {
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(email, password);
//            return authenticationManager.authenticate(authenticationToken);
//        } catch (AuthenticationException e) {
//            throw new AuthenticationException("Authentication failed for user: " + email) {};
//        }
//    }
//
//    @Autowired
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Roles> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//    }


}
