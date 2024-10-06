package com.ecommerce.g58.service;

//import com.ecommerce.g58.dto.UserDTO;
import com.ecommerce.g58.entity.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String email);
    void registerUser(Users users);
    boolean isEmailExist(String email);
}
