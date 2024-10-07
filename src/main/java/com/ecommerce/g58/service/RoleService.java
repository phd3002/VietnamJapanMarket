package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Roles;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;

@Service
public interface RoleService {
    List<Roles> getAllRoles();

    Roles findRoleById(Integer roleId);

    Roles getRoleByName(String roleName);
}
