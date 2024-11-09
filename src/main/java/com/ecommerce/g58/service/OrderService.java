package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable);

    List<OrderManagerDTO> getOrdersForStore(Integer userId);

    void updateOrderStatus(Orders orderId, String newStatus);
}
