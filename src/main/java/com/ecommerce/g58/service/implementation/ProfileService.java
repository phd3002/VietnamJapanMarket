package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    public Users getUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID = " + id + " not found!"));
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

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        user.setPassword(newPassword);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
    }
}
