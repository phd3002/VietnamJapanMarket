package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Roles;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    List<Roles> getAllRoles();

    Roles getRoleById(Integer roleId);
}
