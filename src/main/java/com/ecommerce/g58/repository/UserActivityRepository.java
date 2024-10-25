package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    void deleteByProductId(Products productId);
}
