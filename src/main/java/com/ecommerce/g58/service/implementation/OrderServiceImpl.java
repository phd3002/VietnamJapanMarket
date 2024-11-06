package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.ShippingStatus;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.ShippingStatusRepository;
import com.ecommerce.g58.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

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
            return dto;
        });
    }

    @Override
    public List<OrderManagerDTO> getOrdersForStore(Integer userId) {
        List<Object[]> results = orderRepository.findOrdersByStoreUserId(userId);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO dto = new OrderManagerDTO();
            dto.setOrderId((Integer) result[0]);
            dto.setCustomerName((String) result[1]);
            dto.setProductNames((String) result[2]);
            if (result[3] instanceof BigDecimal) {
                dto.setTotalProducts(((BigDecimal) result[3]).intValue());
            } else if (result[3] instanceof Integer) {
                dto.setTotalProducts((Integer) result[3]);
            }
            if (result[4] instanceof BigDecimal) {
                dto.setTotalPrice(((BigDecimal) result[4]).intValue());
            } else if (result[4] instanceof Integer) {
                dto.setTotalPrice((Integer) result[4]);
            }
            dto.setLatestStatus((String) result[5]);
            orders.add(dto);
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(Orders orderId, String newStatus) {
        ShippingStatus status = new ShippingStatus();
        status.setOrderId(orderId);
        status.setStatus(newStatus);
        status.setUpdatedAt(LocalDateTime.now());
        shippingStatusRepository.save(status);
    }
}
