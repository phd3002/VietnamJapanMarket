package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product-list")
    public String productList(Model model) {
        var products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/product-detail")
    public String productDetail() {
        return "product-detail";
    }

    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId, Model model) {
        List<Products> products = productService.getProductsByCategory(categoryId);
        model.addAttribute("products", products);
        return "product-list";
    }
}
