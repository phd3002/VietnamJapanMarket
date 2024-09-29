package com.ecommerce.g58.controller;
import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping({"/","/homepage"})
    public String showHomePage(Model model) {
        model.addAttribute("latestProducts", productService.getLatest5Products());
        model.addAttribute("categories", categoriesService.getAllCategories());
        return "homepage";
    }
}
