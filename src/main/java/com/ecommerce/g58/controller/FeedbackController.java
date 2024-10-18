package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.FeedbackService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    @PostMapping("/create_new_feedback_store")
    public String createFeedbackStore(@RequestParam String comment, @RequestParam Stores storeId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProductVariation productVariationId = null;
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Feedback feedback = feedbackService.createFeedback(user, comment, productVariationId, storeId);
        return "redirect:/store_details?id=" + storeId;
    }

    @PostMapping("/create_new_feedback_product")
    public String createFeedbackProduct(@RequestParam String comment, @RequestParam Stores storeId, @RequestParam ProductVariation productVariationId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userService.findByEmail(email);
        Feedback feedback = feedbackService.createFeedback(user, comment, productVariationId, storeId);
        return "redirect:/product_details?id=" + productVariationId;
    }

    @GetMapping("/feedback-system-form/{userId}")
    public String feedbackSystemForm(@RequestParam("userId") int userId, Model model) {
        Users user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/sign-in";
        }
        model.addAttribute("user", user);
        model.addAttribute("feedback", new Feedback());
        return "feedback-system-form";
    }

    @PostMapping("/submit-feedback/{userId}")
    public String submitFeedback(@RequestParam("userId") int userId, @RequestParam String comment) {
        try {
            Users user = userService.getUserById(userId);
            if (user == null) {
                return "redirect:/login";
            }
            Feedback feedback = feedbackService.createFeedback(user, comment, null, null);
            return "redirect:/feedback-system-form/" + userId;
        } catch (Exception e) {
            return "redirect:/login";
        }
    }
}
