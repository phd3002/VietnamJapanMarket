package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Users getUserByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User with email = " + email + " not found!");
        return user;
    }

    @Transactional
    public void updateUser(Integer userId,
                           String firstName,
                           String lastName,
                           String email,
                           String phoneNumber,
                           String password,
                           String newPassword) {
        var user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID = " + userId + " not found!"));

        if (!password.isEmpty()) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Incorrect password");
            }

            if (newPassword.length() < 4) {
                throw new RuntimeException("Password must be at least 4 characters");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }

        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
    }
}
