package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.OTP;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Tokens;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.SystemRoles;
import com.ecommerce.g58.repository.OTPRepository;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.TokenRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.utils.UrlUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final OTPRepository OTPRepository;
    private final TokenRepository TokenRepository;
    private final JavaMailSender mailSender;

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean isUsernameExist(String username) {
        return userRepository.findByUsername(username) != null;
    }

//    @Override
//    public void saveUser(Users user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setCreatedAt(LocalDateTime.now());
//        userRepository.save(user);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRoleId().getRoleName())));
    }


    @Transactional
    public void registerUser(Users users) {
        // Mã hóa mật khẩu
        users.setUsername(users.getUsername());
        users.setEmail(users.getEmail());
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setCreatedAt(LocalDateTime.now());

        // Gán role mặc định
        Roles role = roleRepository.findByRoleId(SystemRoles.CUSTOMER);// Lấy role người dùng mặc định
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        } else {
            users.setRoleId(role);
        }
        userRepository.save(users);
    }

    @Override
    public void saveTemporaryUser(Users users, String otpCode) {
        // Save the user temporarily in the users table
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setStatus("TEMP");  // Mark the user as temporary before OTP validation
        userRepository.save(users);

        // Save the OTP in the otp table
        OTP otp = new OTP();
        otp.setUserId(users);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        OTPRepository.save(otp);
    }

    @Override
    public void sendResetPasswordEmail(String email, HttpServletRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        Tokens resetToken = new Tokens(token, user);
        TokenRepository.save(resetToken);


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
        Tokens resetToken = TokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }


    @Override
    //truyền vào string token và password sẽ nhập vào
    public void updatePasswordReset(String token, String password) {
        //get thông tin của token đó
        Tokens resetToken = TokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        Users user = resetToken.getUserId();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);

        TokenRepository.delete(resetToken);
    }

    @Override
    public void updatePassword(Users user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Roles> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//    }


}
