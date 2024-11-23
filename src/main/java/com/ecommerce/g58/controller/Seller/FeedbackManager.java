package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.service.FeedbackService;
import com.ecommerce.g58.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class FeedbackManager {
    private final FeedbackService feedbackService;
    private final UserService userService;

    @GetMapping("/feedback-manager")
    public String feedbackManager(Model model, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        List<Feedback> feedbacks = feedbackService.findAllFByUserId(user.getUserId());
        model.addAttribute("feedbacks", feedbacks);
        return "seller/feedback-manager";
    }

    @GetMapping("/reply-feedback/{id}")
    public String replyFeedback(Model model, @PathVariable Integer id) {
        var feedback = feedbackService.findById(id);
        model.addAttribute("feedback", feedback);
        return "seller/reply-feedback";
    }

    @GetMapping("/seller-feedback")
    @ResponseStatus(HttpStatus.OK)
    public void addFeedback(@RequestParam("reply_content") String feedback,
                              @RequestParam("feedback_id") Integer feedbackId) {
        feedbackService.addSellerReply(feedbackId, feedback);
    }
}
