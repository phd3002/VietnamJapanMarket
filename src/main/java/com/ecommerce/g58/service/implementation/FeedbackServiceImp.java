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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImp implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback findById(Integer id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    @Override
    public List<Feedback> findByProductId(Integer id) {
        return feedbackRepository.findAllByVariationIdProductIdProductId(id);
    }

    @Override
    public List<Feedback> findAllFByUserId(Integer id) {
        return feedbackRepository.findAllByVariationId_ProductId_StoreId_OwnerId_UserId(id);
    }

    @Override
    @Transactional
    public void addSellerReply(Integer feedbackId, String reply) {
        var feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback == null) throw new RuntimeException("Feedback not found");
        feedback.setSellerFeedback(reply);
    }
}
