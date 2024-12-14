package com.ecommerce.g58.controller.Admin;

import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.RoleService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

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

        List<Users> usersPage = userService.getAllUsers();
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("users", usersPage);
        model.addAttribute("roles", roles);
//        model.addAttribute("totalPages", usersPage.getTotalPages());
        return "admin/user-manager";
    }

    @GetMapping("/admin/edit-user/{userId}")
    public String editUser(@PathVariable Integer userId, Model model) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        Users user = userService.getUserById(userId);
        List<Roles> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin/update-user";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam Integer userId, @RequestParam String status, @RequestParam Integer roleId, @RequestParam String username, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String phoneNumber, Model model) {
        if (userId == null || roleId == null) {
            throw new IllegalArgumentException("User ID and Role ID must not be null");
        }
        Users user = userService.getUserById(userId);
        Roles role = roleService.getRoleById(roleId);
        if (user != null && role != null) {
            user.setRoleId(role);
            user.setStatus(status);
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            userService.updateUser(user);
            model.addAttribute("message", "User updated successfully");
        } else {
            model.addAttribute("error", "User or Role not found");
        }
        return "redirect:/admin/user-manager";
    }
}
