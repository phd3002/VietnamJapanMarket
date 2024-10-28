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
        logger.info("Fetching orders for userId: {}, status: {}, page: {}", userId, status, pageable.getPageNumber());

        Page<Object[]> results = orderRepository.findOrdersByUserIdAndStatus(userId, status, pageable);

        logger.info("Fetched {} records from the database.", results.getTotalElements());

        return results.map(result -> {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId((int) result[0]);

            Timestamp orderDateTimestamp = (Timestamp) result[1];
            dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
            dto.setStatus((String) result[2]);

            // Convert total quantity directly to Integer
            if (result[3] instanceof Integer) {
                dto.setTotalQuantity((Integer) result[3]);
            } else if (result[3] instanceof Long) {
                dto.setTotalQuantity(((Long) result[3]).intValue());
            }

            // Convert total price directly to Long
            if (result[4] instanceof Integer) {
                dto.setTotalPrice(((Integer) result[4]).longValue());
            } else if (result[4] instanceof Long) {
                dto.setTotalPrice((Long) result[4]);
            }

            logger.debug("Mapped result to OrdersDTO: {}", dto);
            return dto;
        });
    }
}
