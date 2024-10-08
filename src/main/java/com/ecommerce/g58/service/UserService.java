package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    //    UserDetails loadUserByUsername(String email);
    Users findByEmail(String email);

    void registerUser(Users users);

    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);

    void saveTemporaryUser(Users users, String otpCode);

    //forgot pass
    void sendResetPasswordEmail(String email, HttpServletRequest request);

    boolean isResetTokenValid(String token);

    void updatePasswordReset(String token, String password);

    //change pass
    void updatePassword(Users user, String newPassword);
}
