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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID = " + userId + "!"));

        // Kiểm tra thông tin đầu vào
//        if (firstName == null || firstName.isEmpty() || firstName.length() > 50) {
//            throw new RuntimeException("Họ không được để trống và không được vượt quá 50 ký tự.");
//        }
//        if (lastName == null || lastName.isEmpty() || lastName.length() > 50) {
//            throw new RuntimeException("Tên không được để trống và không được vượt quá 50 ký tự.");
//        }
//        if (email == null || email.isEmpty() || email.length() > 100) {
//            throw new RuntimeException("Email không được để trống và không được vượt quá 100 ký tự.");
//        }
//        if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() > 10) {
//            throw new RuntimeException("Số điện thoại không được để trống và không được vượt quá 10 ký tự.");
//        }


//            if (!passwordEncoder.matches(password, user.getPassword())) {
//                throw new RuntimeException("Mật khẩu không đúng.");
//            }
//
//            if (newPassword.length() < 6) {
//                throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự.");
//            }

            user.setPassword(passwordEncoder.encode(newPassword));


        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
    }

}
