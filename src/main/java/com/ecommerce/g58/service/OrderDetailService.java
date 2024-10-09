package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<OrderDetailDTO> getOrderDetails(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetails = new ArrayList<>();

        for (Object[] result : results) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setProductName((String) result[1]);
            dto.setProductImage((String) result[2]);
            dto.setCategoryName((String) result[3]);
            dto.setSizeAndColor((String) result[4]);
            dto.setOrderTotalPrice(((BigDecimal) result[5]));
            dto.setProductPrice(((BigDecimal) result[6]));
            dto.setAvgRating(((Integer) result[7]));
            dto.setStoreName((String) result[8]);
            dto.setTotalAmount(((BigDecimal) result[9]));
            dto.setShippingFee(((BigDecimal) result[10]));
            dto.setPaymentMethod((String) result[11]);
            dto.setPaymentStatus((String) result[12]);
            dto.setShippingAddress((String) result[13]);
            dto.setShippingStatus((String) result[14]);
            dto.setTrackingNumber((String) result[15]);
            orderDetails.add(dto);
        }
        return orderDetails;
    }
}
