package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(Users user, String comment, ProductVariation variationId, Stores storeId);

    List<Feedback> getFeedbacksByCanteenIdAndStatus(Integer canteenId, FeedbackStatus feedbackStatus);

    List<Feedback> getFeedbacksByFoodIdAndStatus(Integer foodId, FeedbackStatus feedbackStatus);

    Page<Feedback> getFeedbacksByCanteen(Integer canteenId, FeedbackStatus status, Pageable pageable);

    void updateFeedbackStatus(Integer feedbackId, FeedbackStatus status);

    void createFeedbackSystem(Users user, String comment, ProductVariation variationId, Stores storeId, FeedbackStatus feedbackStatus);

    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);

    Page<Feedback> findByStatusAndDateRange(FeedbackStatus status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
