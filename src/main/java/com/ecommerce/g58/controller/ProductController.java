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
import org.springframework.data.domain.Sort;
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
        List<ProductDetailDTO> listProductDetails = productService.getProductDetailByProductIdAndColorId(productId, colorId);
        // Pass the first product detail as the default detail
        ProductDetailDTO productDetail = listProductDetails.get(0);
        if (productDetail == null) {
            return "error";  // Handle product not found
        }


        // Fetch available sizes
        List<String> availableSizes = productService.getAvailableSizesByProductIdAndColorId(productId, colorId);
        List<Color> availableColors = productService.getAvailableColors(productId);
        // Pass the product details, available colors, and sizes to the front-end
        model.addAttribute("productDetail", productDetail);
        model.addAttribute("listProductDetails", listProductDetails);
        model.addAttribute("colorId", colorId);
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
    public String getProductsByCategory(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            @RequestParam(value = "rating", required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort, // Thêm tham số sort
            Model model) {

        // Xử lý mức giá từ priceRange
        Integer minPrice = null;
        Integer maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            if (priceRange.endsWith("+")) {
                // Trường hợp "30+", chỉ đặt minPrice và bỏ qua maxPrice
                minPrice = Integer.parseInt(priceRange.replace("+", "")) * 1000000; // Nhân với 1,000,000
                maxPrice = null;
            } else {
                String[] prices = priceRange.split("-");
                try {
                    minPrice = Integer.parseInt(prices[0]) * 1000000; // Nhân với 1,000,000
                    if (prices.length > 1 && !prices[1].isEmpty()) {
                        maxPrice = Integer.parseInt(prices[1]) * 1000000; // Nhân với 1,000,000
                    }
                } catch (NumberFormatException e) {
                    // Xử lý ngoại lệ nếu giá không hợp lệ
                    minPrice = null;
                    maxPrice = null;
                }
            }
        }

        // Xử lý sắp xếp
        Sort sortOrder;
        switch (sort) {
            case "name":
                sortOrder = Sort.by("productName").ascending();
                break;
            case "priceAsc":
                sortOrder = Sort.by("price").ascending();
                break;
            case "priceDesc":
                sortOrder = Sort.by("price").descending();
                break;
            case "rating":
                // Sắp xếp theo rating trung bình
                sortOrder = Sort.by(Sort.Direction.DESC, "price"); // Thay thế bằng thuộc tính cần sắp xếp nếu có
                break;
            case "latest":
            default:
                sortOrder = Sort.by("createdAt").descending();
                break;
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Products> productPage = productService.getProductsFiltered(search, categoryId, minPrice, maxPrice, minRating, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("rating", minRating);
        model.addAttribute("sort", sort);

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
