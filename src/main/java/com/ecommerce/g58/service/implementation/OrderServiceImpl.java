package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

//    @Override
//    public List<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status) {
//        // Use the query that filters by userId
//        List<Object[]> results = orderRepository.findOrdersByUserIdAndStatus(userId, status);
//        List<OrdersDTO> orders = new java.util.ArrayList<>();
//
//        for (Object[] result : results) {
//            OrdersDTO dto = new OrdersDTO();
//            dto.setOrderId((int) result[0]);
//            Timestamp orderDateTimestamp = (Timestamp) result[1];
//            dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
//            dto.setStatus((String) result[2]);
//            if (result[3] instanceof BigInteger) {
//                dto.setTotalQuantity(new BigDecimal((BigInteger) result[3]));
//            } else if (result[3] instanceof BigDecimal) {
//                dto.setTotalQuantity((BigDecimal) result[3]);
//            }
//            if (result[4] instanceof BigInteger) {
//                dto.setTotalPrice(new BigDecimal((BigInteger) result[4]));
//            } else if (result[4] instanceof BigDecimal) {
//                dto.setTotalPrice((BigDecimal) result[4]);
//            }
//            orders.add(dto);
//        }
//        return orders;
//    }

//    @Override
//    public Integer getLoggedInUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName(); // Assuming you use email for login
//
//        // Call your user service to get the user by email
//        Users user = userService.findByEmail(email);
//        return user.getUserId();
//    }

    @Override
    public Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable) {
        Page<Object[]> results = orderRepository.findOrdersByUserIdAndStatus(userId, status, pageable);
        return results.map(result -> {
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

            return dto;
        });
    }

//    @Override
//    public List<OrdersDTO> getOrderSummariesForLoggedInUser() {
//        Integer userId = getLoggedInUserId(); // Lấy user ID của người dùng đang đăng nhập
//        String status = ""; // Bạn có thể truyền giá trị status cụ thể hoặc để trống nếu muốn lấy tất cả
//        Pageable pageable = PageRequest.of(0, 10); // Phân trang, ví dụ: lấy trang đầu tiên với 10 đơn hàng
//
//        // Gọi phương thức getOrdersByUserIdAndStatus và lấy danh sách các OrdersDTO từ kết quả phân trang
//        return getOrdersByUserIdAndStatus(userId, status, pageable).getContent();
//    }
}
