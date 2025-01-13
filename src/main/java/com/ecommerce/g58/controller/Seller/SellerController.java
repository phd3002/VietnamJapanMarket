package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.BestSellingDTO;
import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.CountryService;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private OrderService orderService;

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
                                @RequestParam String storeAddress,
                                @RequestParam String storeTown,
                                @RequestParam String storeCity, @RequestParam String storeDistrict,
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
        if (store.getStorePhone() == null || store.getStorePhone().isEmpty() || store.getStorePhone().length() > 20|| !store.getStorePhone().matches("^[0-9]*$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại cửa hàng không được để trống ,không được vượt quá 20 ký tự và không có kí tự đặc biệt.");
            return "redirect:/sign-up-seller";
        }
        if (store.getStoreAddress() == null || store.getStoreAddress().isEmpty() || store.getStoreAddress().length() > 255) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa chỉ cửa hàng không được để trống và không được vượt quá 255 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (storeCity == null || storeCity.isEmpty() || storeCity.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thành phố không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (storeDistrict == null || storeDistrict.isEmpty() || storeDistrict.length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quận/huyện không được để trống và không được vượt quá 100 ký tự.");
            return "redirect:/sign-up-seller";
        }
        if (postalCode != null && postalCode.length() > 20) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã bưu chính không được vượt quá 20 ký tự.");
            return "redirect:/sign-up-seller";
        }
        String  detailAddress = storeTown + "-" + storeAddress.trim();
        store.setOwnerId(user);
        store.setStoreMail(user.getEmail()); // Automatically assign the store's email to the user's email
        store.setCity(storeCity);
        store.setDistrict(storeDistrict);
        store.setStoreAddress(detailAddress);
        store.setPostalCode(postalCode != null ? postalCode.replaceAll("^,\\s*|,\\s*$", "").trim() : null);

        Countries country = countryService.getCountryById(2);
        store.setCountry(country); // Set country
        Optional<Stores> existingStoreByName = storeService.findByStoreName(store.getStoreName());
        if (existingStoreByName.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên cửa hàng đã tồn tại. Vui lòng chọn tên khác");
            return "redirect:/sign-up-seller";
        }

        storeService.registerStore(store);

        // Update user role to Seller
        storeService.updateUserRoleToSeller(store.getOwnerId());
        redirectAttributes.addFlashAttribute("successMessage", "Vui lòng đăng nhập lại để truy cập vào cửa hàng của bạn");
        return "redirect:/logout";
    }

    @GetMapping("/seller/dashboard")
    public String showSellerDashboard(Model model,
                                      @RequestParam(value = "startDate", required = false) String startDate,
                                      @RequestParam(value = "endDate", required = false) String endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Users user = userService.findByEmail(userDetails.getUsername());
        Integer userId = user.getUserId();
        Optional<Stores> store = storeService.findByOwnerId(user);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Integer totalRevenue = storeService.calculateTotalRevenue(userId, startDate, endDate);
        Integer totalProductSold = storeService.totalProductsSold(userId, startDate, endDate);
        Integer totalOrders = storeService.totalOrders(userId, startDate, endDate);
        Integer totalOrdersCompleted = storeService.totalOrdersCompleted(userId, startDate, endDate);
        Integer totalOrdersCancelledAndReturned = storeService.totalOrdersCancelledAndReturned(userId, startDate, endDate);
        List<BestSellingDTO> bestSellingProducts = storeService.getBestSellingProducts(userId, startDate, endDate);
        Integer count5StarFeedback = storeService.count5StarFeedback(userId, startDate, endDate);
        Integer count4StarFeedback = storeService.count4StarFeedback(userId, startDate, endDate);
        Integer count3StarFeedback = storeService.count3StarFeedback(userId, startDate, endDate);
        Integer count2StarFeedback = storeService.count2StarFeedback(userId, startDate, endDate);
        Integer count1StarFeedback = storeService.count1StarFeedback(userId, startDate, endDate);
        Integer totalRevenueCurrentMonth = storeService.totalRevenueCurrentMonth(userId);
        Integer totalRevenueLastMonth = storeService.totalRevenueLastMonth(userId);
        Integer totalRevenueLast2Months = storeService.totalRevenueLast2Months(userId);
        Integer totalRevenueLast3Months = storeService.totalRevenueLast3Months(userId);
        Integer totalRevenueLast4Months = storeService.totalRevenueLast4Months(userId);
        Integer totalRevenueLast5Months = storeService.totalRevenueLast5Months(userId);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US); // Định dạng theo locale US
        String formattedTotalRevenue = numberFormat.format(totalRevenue);

        model.addAttribute("totalRevenue", formattedTotalRevenue);
        model.addAttribute("totalProducts", totalProductSold);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalOrdersCompleted", totalOrdersCompleted);
        model.addAttribute("totalOrdersCancelledAndReturned", totalOrdersCancelledAndReturned);
        model.addAttribute("bestSellingProducts", bestSellingProducts);
        model.addAttribute("count5StarFeedback", count5StarFeedback);
        model.addAttribute("count4StarFeedback", count4StarFeedback);
        model.addAttribute("count3StarFeedback", count3StarFeedback);
        model.addAttribute("count2StarFeedback", count2StarFeedback);
        model.addAttribute("count1StarFeedback", count1StarFeedback);
        model.addAttribute("totalRevenueCurrentMonth", totalRevenueCurrentMonth);
        model.addAttribute("totalRevenueLastMonth", totalRevenueLastMonth);
        model.addAttribute("totalRevenueLast2Months", totalRevenueLast2Months);
        model.addAttribute("totalRevenueLast3Months", totalRevenueLast3Months);
        model.addAttribute("totalRevenueLast4Months", totalRevenueLast4Months);
        model.addAttribute("totalRevenueLast5Months", totalRevenueLast5Months);

//        Optional<Stores> store = storeService.findByOwnerId(user);
        store.ifPresent(value -> model.addAttribute("storeId", value.getStoreId()));
        return "seller/dashboard";
    }
}

