package com.ecommerce.g58.service.impl;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrdersDTO> getOrderSummariesByUserId(Integer userId) {
        // Use the query that filters by userId
        List<Object[]> results = orderRepository.findOrdersByUserId(userId);
        List<OrdersDTO> orders = new java.util.ArrayList<>();

        for (Object[] result : results) {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId((int) result[0]);
            Timestamp orderDateTimestamp = (Timestamp) result[1];
            dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
            dto.setStatus((String) result[2]);
            if (result[3] instanceof BigInteger) {
                dto.setTotalQuantity(new BigDecimal((BigInteger) result[3]));
            } else if (result[3] instanceof BigDecimal) {
                dto.setTotalQuantity((BigDecimal) result[3]);
            }
            if (result[4] instanceof BigInteger) {
                dto.setTotalPrice(new BigDecimal((BigInteger) result[4]));
            } else if (result[4] instanceof BigDecimal) {
                dto.setTotalPrice((BigDecimal) result[4]);
            }
            orders.add(dto);
        }
        return orders;
    }

    @Override
    public Integer getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Assuming you use email for login

        // Call your user service to get the user by email
        Users user = userService.findByEmail(email);
        return user.getUserId();
    }

    @Override
    public List<OrdersDTO> getOrderSummariesForLoggedInUser() {
        Integer userId = getLoggedInUserId(); // Get the user ID of the logged-in user
        return getOrderSummariesByUserId(userId);
    }
}
