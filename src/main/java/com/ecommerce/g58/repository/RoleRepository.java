package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    @Query("SELECT r FROM Roles r WHERE r.roleName = ?1")
    Roles findByName(String name);

    Roles findByRoleId(Integer roleId);
}
