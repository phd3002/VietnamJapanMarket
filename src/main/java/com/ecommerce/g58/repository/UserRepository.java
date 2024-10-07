package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // Tìm kiếm người dùng theo tên đăng nhập (username)
//    Users findByUsername(String username);
    Users findByEmail(String email);
    @Query(value = "SELECT\n" +
            "u.user_id, u.username, u.email, r.role_id, r.role_name\n" +
            "FROM users u\n" +
            "LEFT JOIN roles r ON u.role_id = r.role_id\n" +
            "WHERE u.status = 'active'\n" +
            "AND u.username = :userName ", nativeQuery = true)
    Users findByUsername(@Param("userName") String username);
//    Optional<Users> findByUsername(String username);
}
