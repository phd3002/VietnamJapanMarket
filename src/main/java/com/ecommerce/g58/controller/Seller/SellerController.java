package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.CountryService;
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
import org.springframework.ui.Model;
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


    @Autowired
    private CountryService countryService;

    @GetMapping("/sign-up-seller")
    public String signUpSellerForm(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn phải đăng nhập để có thể đăng kí bán hàng");
            return "redirect:/sign-in";
        }

        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Optional<Stores> existingStore = storeService.findByOwnerId(user);
        if (existingStore.isPresent()) {
            return "redirect:/seller/dashboard";
        }

        // Adding available countries to model
        model.addAttribute("countries", countryService.getAllCountries());

        return "sign-up-seller";
    }

    @PostMapping("/sign-up-seller")
    public String registerStore(@ModelAttribute Stores store,
                                @RequestParam("countryId") Integer countryId,
                                @RequestParam("city") String city,
                                @RequestParam("district") String district,
                                @RequestParam("postalCode") String postalCode,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        store.setOwnerId(user);
        store.setStoreMail(user.getEmail()); // Automatically assign the store's email to the user's email
        store.setCity(city != null ? city.replaceAll("^,\\s*|,\\s*$", "").trim() : null);
        store.setDistrict(district != null ? district.replaceAll("^,\\s*|,\\s*$", "").trim() : null);
        store.setPostalCode(postalCode != null ? postalCode.replaceAll("^,\\s*|,\\s*$", "").trim() : null);

        Countries country = countryService.getCountryById(countryId);
        store.setCountry(country); // Set country
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
    public String showSellerDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Optional<Stores> store = storeService.findByOwnerId(user);
        store.ifPresent(value -> model.addAttribute("storeId", value.getStoreId()));
        return "seller/dashboard";
    }
}

