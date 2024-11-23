package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.dto.ProductDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ecommerce.g58.service.FeedbackService;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final FeedbackService feedbackService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/product-list")
    public String getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Products> productPage = productService.findAllProducts(pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

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
//        System.out.println("Available colors: " + availableColors);
//        System.out.println("Available sizes: " + availableSizes);
//        System.out.println("Selected size: " + sizeId);
//        System.out.println(productDetail.getVariationId());
//        System.out.println(productDetail.getStoreId());
        // Pass the product details, available colors, and sizes to the front-end
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("availableColors", availableColors);
        model.addAttribute("availableSizes", availableSizes);
        model.addAttribute("selectedSize", sizeId);  // Add selected size to the model
        model.addAttribute("feedbacks", feedbackService.findByProductId(productId));
        model.addAttribute("product", productService.getProductById(productId));
        double averageRating = feedbackService.findByProductId(productId).stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0);
        model.addAllAttributes(calculateStars(averageRating));
        model.addAttribute("avgText", averageRating == 0 ? "" : new DecimalFormat("#.0").format(averageRating));

        return "product-detail";
    }

//    @GetMapping("/seller-products")
//    public String getProductsByStore(@RequestParam Stores storeId, Model model) {
//        List<Products> products = productService.getProductsByStoreId(storeId);
//        model.addAttribute("products", products);
//        return "seller/product-manager";
//    }

    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId, Model model) {
        logger.info("Fetching products for category ID: {}", categoryId);
        List<Products> products = productService.getProductsByCategory(categoryId);
        logger.info("Number of products found: {}", products.size());
        model.addAttribute("products", products);
        return "product-list";
    }

    private Map<String, Integer> calculateStars(double averageRating) {
        Map<String, Integer> stars = new HashMap<>();
        int fullStars = (int) Math.floor(averageRating);
        boolean hasHalfStar = (averageRating - fullStars) >= 0.5;
        int halfStars = hasHalfStar ? 1 : 0;
        int emptyStars = 5 - fullStars - halfStars;
        stars.put("fullStars", fullStars);
        stars.put("halfStars", halfStars);
        stars.put("emptyStars", emptyStars);
        return stars;
    }
}
