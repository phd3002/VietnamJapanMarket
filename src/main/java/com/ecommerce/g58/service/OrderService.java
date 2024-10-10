package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrdersDTO> getOrderSummaries() {
        List<Object[]> results = orderRepository.findOrderSummaries();
        List<OrdersDTO> orders = new java.util.ArrayList<>();
        for (Object[] result : results) {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId((int) result[0]);
            Timestamp orderDateTimestamp = (Timestamp) result[1];
            dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
            dto.setStatus((String) result[2]);
            dto.setTotalQuantity(((BigDecimal) result[3]).intValue());
            dto.setTotalPrice((BigDecimal) result[4]);
            orders.add(dto);
        }
        return orders;
    }
}
