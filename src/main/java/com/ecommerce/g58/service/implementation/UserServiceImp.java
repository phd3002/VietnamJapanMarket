package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.OTP;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.SystemRoles;
import com.ecommerce.g58.repository.OTPRepository;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
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
    public UserDetails loadUserByUsername(String email) {
        return null;
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

//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Roles> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//    }


}
