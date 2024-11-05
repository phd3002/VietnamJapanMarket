package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.RoleService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserManagementController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/user-manager")
    public String getAllUsers(Model model) {
        List<Users> users = userService.getAllUsers();
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "admin/user-manager";
    }

    @GetMapping("/admin/user-manager/edit/{userId}")
    public String editUser(@PathVariable Integer userId, Model model) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        Users user = userService.getUserById(userId);
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin/user-manager :: #ModalUP";
    }

    @PostMapping("/admin/user-manager/update-user")
    public String updateUser(@RequestParam Integer userId, @RequestParam String status, @RequestParam Integer roleId, Model model) {
        if (userId == null || roleId == null) {
            throw new IllegalArgumentException("User ID and Role ID must not be null");
        }
        Users user = userService.getUserById(userId);
        Roles role = roleService.getRoleById(roleId);
        if (user != null && role != null) {
            user.setRoleId(role);
            user.setStatus(status);
            userService.updateUser(user);
            model.addAttribute("message", "User updated successfully");
        } else {
            model.addAttribute("error", "User or Role not found");
        }
        return "redirect:/admin/user-manager";
    }
}
