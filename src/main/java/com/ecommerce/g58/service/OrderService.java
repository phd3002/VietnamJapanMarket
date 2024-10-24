package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrdersDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // Method to get order summaries for a specific user
//    List<OrdersDTO> getOrderSummariesByUserId(Integer userId);

    // Method to get the logged-in user's ID
//    Integer getLoggedInUserId();

    // Method to get order summaries for the logged-in user
//    List<OrdersDTO> getOrderSummariesForLoggedInUser();
//    List<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status);
    Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable);
}
