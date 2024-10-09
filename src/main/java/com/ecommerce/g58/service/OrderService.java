package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrdersDTO> getOrderDetails() {
        return orderRepository.findOrderDetails();
    }
}
