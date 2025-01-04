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
        // Check if the user is logged in
        if (principal != null) {
            Users user = userService.findByEmail(principal.getName());
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("firstname", user.getFirstName());
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        // Use getAllProducts() method from ProductService
        List<Products> products = productService.get12HighestOrderProducts();
        List<Products> highestStarProduct = productService.getTop12ProductsByHighestRatingFromActiveStores();
        List<Categories> categories = categoriesService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("highestStarProduct", highestStarProduct);
        model.addAttribute("categories", categories);

        return "homepage";
    }
}
