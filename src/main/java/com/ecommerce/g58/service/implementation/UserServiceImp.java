package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.UserDTO;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Users registerUser(UserDTO userDTO) throws Exception {
//        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
//            throw new Exception("Email already registered!");
//        }
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new Exception("Username already taken!");
        }
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new Exception("Passwords do not match!");
        }

        Users user = new Users();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setStatus("active");

        Roles role = roleRepository.findByName("ROLE_CUSTOMER");
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        } else {
            role.setRoleId(role.getRoleId());
        }

        return userRepository.save(user);
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
