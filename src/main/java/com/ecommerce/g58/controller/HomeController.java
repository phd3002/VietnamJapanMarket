package com.ecommerce.g58.controller;


import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/homepage"})
    public String showHomePage(Model model, Principal principal) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (principal != null) {
            Users user = userService.findByEmail(principal.getName());  // Fetch user details by email
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", user.getUsername());  // Pass firstName instead of email
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        List<Categories> categories = categoriesService.getAllCategories();
        List<ProductDTO> productDetails = productService.getProductDetails();
        List<ProductDTO> searchProduct = productService.getSearchProduct();
        model.addAttribute("productDetails", productDetails);
        model.addAttribute("searchProduct", searchProduct);
        model.addAttribute("categories", categories);
        return "homepage";
    }
}
