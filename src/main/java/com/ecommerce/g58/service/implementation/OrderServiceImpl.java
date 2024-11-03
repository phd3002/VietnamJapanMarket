package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable) {
        Page<Object[]> results = orderRepository.findOrdersByUserIdAndStatus(userId, status, pageable);
        return results.map(result -> {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId((int) result[0]);

            if (result[1] != null) {
                Timestamp orderDateTimestamp = (Timestamp) result[1];
                dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
            }

            dto.setStatus((String) result[2]);

            if (result[3] != null) {
                if (result[3] instanceof Number) {
                    dto.setTotalQuantity(((Number) result[3]).intValue());
                }
            }

            if (result[4] != null) {
                if (result[4] instanceof Number) {
                    dto.setTotalPrice(((Number) result[4]).longValue());
                }
            }

            logger.info("Order ID: {}, Total Quantity: {}, Total Price: {}", dto.getOrderId(), dto.getTotalQuantity(), dto.getTotalPrice());
            return dto;
        });
    }
}
