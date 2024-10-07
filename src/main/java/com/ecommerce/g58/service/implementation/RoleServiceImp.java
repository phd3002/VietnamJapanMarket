package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImp implements RoleService {
    @Autowired
private RoleRepository roleRepository;

    @Override
    public List<Roles> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Roles findRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    @Override
    public Roles getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
