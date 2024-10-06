package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // Tìm kiếm người dùng theo tên đăng nhập (username)
//    Users findByUsername(String username);
    Users findByEmail(String email);
    Optional<Users> findByUsername(String username);
}
