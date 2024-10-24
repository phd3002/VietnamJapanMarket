package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderDetailDTO;
import java.util.List;

public interface OrderDetailService {

    // Method to fetch order details by orderId
    List<OrderDetailDTO> getOrderDetails(Long orderId);
}

