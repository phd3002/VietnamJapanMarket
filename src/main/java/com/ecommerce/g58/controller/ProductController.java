package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.service.FeedbackService;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final FeedbackService feedbackService;

    @GetMapping("/product-list")
    public String productList(Model model) {
        var products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    };

    @GetMapping("/product-detail/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("allProducts", productService.getAllProducts());
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("feedbacks", feedbackService.findByProductId(id));

        int averageRating = (int) feedbackService.findByProductId(id).stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0);

        model.addAttribute("averageRating", averageRating);

        return "product-detail";
    }
}
