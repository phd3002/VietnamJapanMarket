package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.FeedbackStatus;
import com.ecommerce.g58.repository.FeedbackRepository;
import com.ecommerce.g58.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImp implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback createFeedback(Users user, String comment, ProductVariation variationId, Stores storeId) {
        Feedback feedback = Feedback.builder()
                .userId(user)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .variationId(variationId)
                .storeId(storeId)
                .feedbackStatus(FeedbackStatus.PENDING)
                .build();
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbacksByCanteenIdAndStatus(Integer canteenId, FeedbackStatus status) {
        return feedbackRepository.findFeedbackByCanteenIdAndStatus(canteenId, status);
    }

    @Override
    public List<Feedback> getFeedbacksByFoodIdAndStatus(Integer foodId, FeedbackStatus feedbackStatus) {
        return feedbackRepository.findFeedbackByFoodIdAndStatus(foodId, feedbackStatus);
    }

    @Override
    public Page<Feedback> getFeedbacksByCanteen(Integer canteenId, FeedbackStatus status, Pageable pageable) {
        return feedbackRepository.findByCanteenIdAndStatus(canteenId, status, pageable);
    }

    @Override
    public void updateFeedbackStatus(Integer feedbackId, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(() -> new IllegalArgumentException("Invalid feedback ID"));
        feedback.setFeedbackStatus(status);
        feedbackRepository.save(feedback);
    }

    @Override
    public void createFeedbackSystem(Users user, String comment, ProductVariation variationId, Stores storeId, FeedbackStatus feedbackStatus) {
        Feedback feedback = new Feedback();
        feedback.setUserId(user);
        feedback.setComment(comment);
        feedback.setVariationId(variationId); // Đây sẽ là null
        feedback.setStoreId(storeId); // Đây sẽ là null
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setFeedbackStatus(feedbackStatus); // Thiết lập trạng thái là VIEWABLE

        feedbackRepository.save(feedback);
    }

    @Override
    public Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable) {
        return feedbackRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Feedback> findByStatusAndDateRange(FeedbackStatus status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
        }
        return feedbackRepository.findByStatusAndDateRange(status, startDate, endDate, pageable);
    }

    @Override
    public List<Feedback> findByProductId(Integer id) {
        return feedbackRepository.findAllByVariationIdProductIdProductId(id);
    }
}
