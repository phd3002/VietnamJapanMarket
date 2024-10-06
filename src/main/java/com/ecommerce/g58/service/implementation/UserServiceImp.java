package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }
    @Override
    public Users registerUser(Users users) {
        // Kiểm tra xem email đã tồn tại chư

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
            users.setRole(role);
        }

        return userRepository.save(users);
    }

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Users user = userRepository.findByEmail(email);
    if (user == null) {
        throw new UsernameNotFoundException("User not found");
    }

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())));
}

//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Roles> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
//    }



}
