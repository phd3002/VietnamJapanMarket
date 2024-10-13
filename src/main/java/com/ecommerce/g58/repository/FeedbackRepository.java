package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("SELECT f FROM Feedback f  WHERE f.storeId = :storeId AND f.feedbackStatus = :status ORDER BY f.createdAt DESC")
    List<Feedback> findFeedbackByCanteenIdAndStatus(@Param("storeId") Integer storeId, @Param("status") FeedbackStatus feedbackStatus);

    @Query("SELECT f FROM Feedback f  WHERE f.variationId = :variationId AND f.feedbackStatus = :status ORDER BY f.createdAt DESC")
    List<Feedback> findFeedbackByFoodIdAndStatus(@Param("variationId") Integer canteenId, @Param("status") FeedbackStatus feedbackStatus);

    @Query("SELECT f FROM Feedback f WHERE f.storeId = :storeId AND f.feedbackStatus = :status ORDER BY f.createdAt DESC" )
    Page<Feedback> findByCanteenIdAndStatus(@Param("storeId") Integer canteenId, @Param("status") FeedbackStatus feedbackStatus, Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.feedbackStatus = :status ORDER BY f.createdAt DESC")
    Page<Feedback> findByStatus(@Param("status") FeedbackStatus status, Pageable pageable);

    @Query("SELECT f FROM Feedback f " +
            "WHERE f.feedbackStatus = :status " +
            "AND f.createdAt BETWEEN :startDate AND :endDate")
    Page<Feedback> findByStatusAndDateRange(
            @Param("status") FeedbackStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    List<Feedback> findAllByVariationIdProductIdProductId(Integer variationId);
}
