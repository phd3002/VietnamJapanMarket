package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAllByVariationIdProductIdProductId(Integer variationId);
    void deleteByUserId_UserId(Integer userId);
    List<Feedback> findAllByVariationId_ProductId_StoreId_OwnerId_UserId(Integer variationId);
    void deleteByVariationId(ProductVariation variationId);
}
