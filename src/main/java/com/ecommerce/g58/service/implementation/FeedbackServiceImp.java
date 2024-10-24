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

    @Override
    public List<Feedback> findByProductId(Integer id) {
        return feedbackRepository.findAllByVariationIdProductIdProductId(id);
    }
}
