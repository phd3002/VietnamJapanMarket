package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrdersDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface OrderService {

    // Method to get order summaries for a specific user
    List<OrdersDTO> getOrderSummariesByUserId(Integer userId);

    // Method to get the logged-in user's ID
    Integer getLoggedInUserId();

    // Method to get order summaries for the logged-in user
    List<OrdersDTO> getOrderSummariesForLoggedInUser();
}
