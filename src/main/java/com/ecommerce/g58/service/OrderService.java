package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrdersDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable);

}
