package com.ecommerce.g58.service;

//import com.ecommerce.g58.dto.UserDTO;

import com.ecommerce.g58.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface UserService extends UserDetailsService {

    Users findByEmail(String email);

    Users getUserById(Integer userId);

    UserDetails loadUserByUsername(String email);

    void registerUser(Users users);

    boolean isEmailExist(String email);

    boolean checkPassword(String rawPassword, String encodedPassword);

    //forgot pass
    void sendResetPasswordEmail(String email, HttpServletRequest request);

    boolean isResetTokenValid(String token);

    void updatePasswordReset(String token, String password);

    //change pass
    void updatePassword(Users user, String newPassword);

//    Users findByEmail(String email);


}
