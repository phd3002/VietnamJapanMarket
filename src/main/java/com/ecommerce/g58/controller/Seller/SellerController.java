package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.BestSellingDTO;
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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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
        // Kiểm tra các thông tin đầu vào
        if (store.getStoreName() == null || store.getStoreName().isEmpty() || store.getStoreName().length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên cửa hàng không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (store.getStorePhone() == null || store.getStorePhone().isEmpty() || store.getStorePhone().length() > 20) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại cửa hàng không được để trống và không được vượt quá 20 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (store.getStoreAddress() == null || store.getStoreAddress().isEmpty() || store.getStoreAddress().length() > 255) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ cửa hàng không được để trống và không được vượt quá 255 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (countryId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quốc gia không được để trống.");
            return "redirect:/sign-up-seller";
        }
        if (city == null || city.isEmpty() || city.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thành phố không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (district == null || district.isEmpty() || district.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quận/huyện không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (postalCode != null && postalCode.length() > 20) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã bưu chính không được vượt quá 20 ký tự.");
            return "redirect:/sign-up-seller";
        }
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
    public String showSellerDashboard(Model model,
                                      @RequestParam(value = "startDate", required = false) String startDate,
                                      @RequestParam(value = "endDate", required = false) String endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        Integer userId = user.getUserId();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Integer totalRevenue = storeService.calculateTotalRevenue(userId, startDate, endDate);
        Integer totalProducts = storeService.calculateTotalProducts(userId);
        Integer totalOrders = storeService.totalOrders(userId, startDate, endDate);
        Integer totalOrdersCompleted = storeService.totalOrdersCompleted(userId, startDate, endDate);
        Integer totalOrdersCancelledAndReturned = storeService.totalOrdersCancelledAndReturned(userId, startDate, endDate);
        List<BestSellingDTO> bestSellingProducts = storeService.getBestSellingProducts(userId, startDate, endDate);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US); // Định dạng theo locale US
        String formattedTotalRevenue = numberFormat.format(totalRevenue);
        model.addAttribute("totalRevenue", formattedTotalRevenue);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalOrdersCompleted", totalOrdersCompleted);
        model.addAttribute("totalOrdersCancelledAndReturned", totalOrdersCancelledAndReturned);
        model.addAttribute("bestSellingProducts", bestSellingProducts);
        Optional<Stores> store = storeService.findByOwnerId(user);
        store.ifPresent(value -> model.addAttribute("storeId", value.getStoreId()));
        return "seller/dashboard";
    }
}

