package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Color;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Size;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/product-detail/{productId}")
    public String showProductDetail(@PathVariable("productId") Integer productId,
                                    @RequestParam(value = "colorId", required = false) Integer colorId,
                                    @RequestParam(value = "sizeId", required = false) Integer sizeId,
                                    Model model) {
        if (colorId == null) {
            // Default to the lowest colorId
            List<Color> availableColors = productService.getAvailableColors(productId);
            if (!availableColors.isEmpty()) {
                colorId = availableColors.get(0).getColorId(); // Get the lowest colorId
            }
        }

        // Fetch product details based on productId and colorId
        ProductDetailDTO productDetail = productService.getProductDetailByProductIdAndColorId(productId, colorId);

        if (productDetail == null) {
            return "error";  // Handle product not found
        }

        // Fetch available sizes
        List<String> availableSizes = productService.getAvailableSizesByProductIdAndColorId(productId, colorId);
        List<Color> availableColors = productService.getAvailableColors(productId);
        System.out.println("Available colors: " + availableColors);
        System.out.println("Available sizes: " + availableSizes);
        System.out.println("Selected size: " + sizeId);
        System.out.println(productDetail.getVariationId());
        // Pass the product details, available colors, and sizes to the front-end
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("availableColors", availableColors);
        model.addAttribute("availableSizes", availableSizes);
        model.addAttribute("selectedSize", sizeId);  // Add selected size to the model
        return "product-detail";
    }







    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId, Model model) {
        List<Products> products = productService.getProductsByCategory(categoryId);
        model.addAttribute("products", products);
        return "product-list";
    }
}
