package com.ecommerce.g58.controller;


import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.Categories;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping({"/","/homepage"})
    public String showHomePage(Model model) {
//        List<Products> products = productService.getAllProducts();
//        List<Categories> categories = categoriesService.getAllCategories();
        List<ProductDTO> productDetails = productService.getProductDetails();
        model.addAttribute("productDetails", productDetails);
        model.addAttribute("categories", categoriesService.getAllCategories());

        return "homepage";
    }
}
