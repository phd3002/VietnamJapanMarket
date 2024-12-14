package com.ecommerce.g58.service;

//import com.ecommerce.g58.dto.UserDTO;

import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public interface UserService extends UserDetailsService {

    Users findByEmail(String email);

    Users getUserById(Integer userId);

    UserDetails loadUserByUsername(String email);

    void registerUser(Users users);

    boolean isEmailExist(String email);

    boolean isUsernameExist(String username);

    boolean checkPassword(String rawPassword, String encodedPassword);

    //forgot pass
    void sendResetPasswordEmail(String email, HttpServletRequest request);

    boolean isResetTokenValid(String token);

    void updatePasswordReset(String token, String password);

    //change pass
    void updatePassword(Users user, String newPassword);

    List<Users> getAllUsers();;

    List<Users> getAllSellers();

    List<Users> getAllCustomers();

    void deleteUser(Integer userId);

    boolean isAccountActive(String email);

    long getTotalUsers();

    long getTotalSellers();

    void updateUser(Users user);
}
