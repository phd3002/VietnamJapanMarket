package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductVariationDTO;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product-list")
    public String productList(Model model) {
//        List<ProductVariationDTO> products = productService.getAllProductVariation();
//        var products = productService.getProductDetails();
        List<ProductDTO> products = productService.getAllProducts();
        System.out.println("Product list: " + products);
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/product-detail/{productId}")
    public String productDetail(@PathVariable("productId") Integer productId, Model model) {
        ProductVariationDTO productVariations = productService.getProductVariationsByProductId(productId);
        System.out.println("Product details: " + productVariations);
        model.addAttribute("productVariation", productVariations);
        return "product-detail";
    }

}
