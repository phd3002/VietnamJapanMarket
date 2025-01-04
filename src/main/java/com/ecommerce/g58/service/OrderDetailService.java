package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderDetailDTO;
import org.springframework.stereotype.Service;

import com.ecommerce.g58.dto.OrderDetailManagerDTO;

import java.util.List;
@Service
public interface OrderDetailService {

    // Method to fetch order details by orderId
    List<OrderDetailDTO> getOrderDetails(Long orderId);

    void rateOrder(Long orderId, String userEmail, String rateText, Integer rateStar);

    List<OrderDetailManagerDTO> getOrderDetailsForManager(Long orderId);

    void updateStatus(Integer orderId, String status, String reason);

    boolean refundOrder(Integer orderId);
    boolean changeStatus(Integer orderId, String status, String reason);
    boolean cancelOrder(Integer orderId, String status, String reason);
    boolean rejectOrder(Integer orderId, String status, String reason);
}

