package com.ecommerce.g58.controller;


import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.Categories;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping({"/","/homepage"})
    public String showHomePage(Model model, Principal principal) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
//        if (principal != null) {
//            model.addAttribute("isLoggedIn", true);
//            model.addAttribute("username", principal.getName());  // Tên người dùng
//        } else {
//            model.addAttribute("isLoggedIn", false);
//        }
//        List<Products> products = productService.getAllProducts();
//        List<Categories> categories = categoriesService.getAllCategories();
        List<ProductDTO> productDetails = productService.getProductDetails();
        model.addAttribute("productDetails", productDetails);
        model.addAttribute("categories", categoriesService.getAllCategories());
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated();
        System.out.println(isLoggedIn);
        // Add to the model to be used in the view
        model.addAttribute("isLoggedIn", isLoggedIn);

        // If logged in, also add user details (e.g., username) to the model
        if (isLoggedIn) {
            model.addAttribute("username", authentication.getName());
        }
        return "homepage";
    }
}
