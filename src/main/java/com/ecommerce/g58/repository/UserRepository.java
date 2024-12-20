package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    // Tìm kiếm người dùng theo tên đăng nhập (username)
    Users findByUsername(String username);

    Users findByEmail(String email);

//    Optional<Users> findByUsername(String username);

    List<Users> findAllByRoleId(Roles roleId);

    Optional<Users> findByEmailAndStatus(String email, String status);

    long count();

    Users findFirstByRoleId_RoleId(Integer roleId);
}
