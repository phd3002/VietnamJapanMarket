package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.implementation.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final ProfileService profileService;
    private final StoreService storeService;
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-account")
    public String myAccount(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "redirect:/sign-in"; // Use redirect to avoid returning a relative path
        }

        var email = userDetails.getUsername();
        var user = profileService.getUserByEmail(email);
        if (user == null) {
            return "redirect:/error"; // Handle the case where the user is not found
        }
        model.addAttribute("user", user);
        model.addAttribute("createdAtFormatted", DateTimeFormatter.ofPattern("MMM yyyy").format(user.getCreatedAt()));

        boolean hasStore = storeService.findByOwnerId(user).isPresent();
        model.addAttribute("hasStore", hasStore);

        return "my-account";
    }

    @PostMapping("/update-account")
    public String updateAccount(Principal principal,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String district,
                                @RequestParam(required = false) String ward,
                                @RequestParam(required = false) String address,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            return "/sign-in";
        }

        String email = userDetails.getUsername();
        Users user = userRepository
                .findByEmail(principal.getName());

        // Validate input parameters
        if (firstName == null || firstName.isEmpty() || firstName.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Họ không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/my-account";
        }
        if (lastName == null || lastName.isEmpty() || lastName.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Tên không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/my-account";
        }
        if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() > 20 || !phoneNumber.matches("^[0-9]*$") || phoneNumber.length() < 10) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại không được để trống, không được vượt quá 20 ký tự, phải là số, và phải có ít nhất 10 số.");
            return "redirect:/my-accounto";
        }
        if (address == null || address.isEmpty() || address.length() > 255 || address.contains("-")) {
            redirectAttributes.addFlashAttribute("error", "Địa chỉ không được để trống, không được vượt quá 255 ký tự, và không được chứa ký tự đặc biệt.");
            return "redirect:/my-account";
        }

        // Update user details

//        String detailAddress = ward + "-" + district + "-" + city + "-" + address.trim();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setCity(city);
        user.setDistrict(district);
        user.setWard(ward);
        user.setAddress(address);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        return "redirect:/my-account";
    }

//    @GetMapping("/my-account/post")
//    @ResponseBody
//    @ResponseStatus(HttpStatus.OK)
//    public void myAccountPost(@RequestParam String firstName,
//                              @RequestParam String lastName,
//                              @RequestParam String email,
//                              @RequestParam String phoneNumber,
//                              @RequestParam String password,
//                              @RequestParam String newPassword,
//                              @AuthenticationPrincipal UserDetails userDetails) {
//        var loggedInEmail = userDetails.getUsername();
//        var user = profileService.getUserByEmail(loggedInEmail);
//        profileService.updateUser(user.getUserId(), firstName, lastName, email, phoneNumber, password, newPassword);
//    }
}
