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
    Feedback findById(Integer id);
    List<Feedback> findByProductId(Integer id);
    List<Feedback> findAllFByUserId(Integer id);
    void addSellerReply(Integer feedbackId, String reply);
}
