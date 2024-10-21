package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class SellerController {
    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;


    @GetMapping("/sign-up-seller")
    public String signUpSellerForm(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn phải đăng nhập để có thể đăng kí bán hàng");
            return "redirect:/sign-in";
        }
//        User userDetails = (User) authentication.getPrincipal();
//        Users user = userService.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Optional<Stores> existingStore = storeService.findByOwnerId(user);
//        if (existingStore.isPresent()) {
//            return "redirect:/seller/dashboard";
//        }
        return "sign-up-seller";
    }

    @PostMapping("/sign-up-seller")
    public String registerStore(@ModelAttribute Stores store,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        store.setOwnerId(user);
        Optional<Stores> existingStoreByName = storeService.findByStoreName(store.getStoreName());
        if (existingStoreByName.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên cửa hàng đã tồn tại. Vui lòng chọn tên khác");
            return "redirect:/sign-up-seller";
        }

        storeService.registerStore(store);

        // Update user role to Seller
        storeService.updateUserRoleToSeller(store.getOwnerId());

        return "redirect:/seller/dashboard";
    }

    @GetMapping("/seller/dashboard")
    public String showSellerDashboard() {
        return "seller/dashboard";
    }
}

