package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.repository.OrderDetailRepository;
import com.ecommerce.g58.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetails = new ArrayList<>();

        for (Object[] result : results) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderId(orderId);
            dto.setProductId(((Integer) result[1]).longValue());
            dto.setStoreId(((Integer) result[2]).longValue());
            dto.setProductName((String) result[3]);
            dto.setProductImage((String) result[4]);
            dto.setCategoryName((String) result[5]);
            dto.setSizeAndColor((String) result[6]);
//            dto.setOrderTotalPrice(((Integer) result[7]));
            if (result[7] instanceof BigInteger) {
                dto.setOrderTotalPrice(((BigInteger) result[7]).intValue());
            } else if (result[7] instanceof Integer) {
                dto.setOrderTotalPrice((Integer) result[7]);
            }
            dto.setQuantity((Integer) result[8]);
            dto.setProductTotalPrice((Integer) result[9]);
            dto.setAvgRating((Integer) result[10]);
            dto.setStoreName((String) result[11]);
            dto.setStoreImage((String) result[12]);  // Added
            dto.setTotalAmount((Integer) result[13]);
            dto.setShippingFee((Integer) result[14]);
            dto.setPaymentMethod((String) result[15]);
            dto.setPaymentStatus((String) result[16]);
            dto.setShippingAddress((String) result[17]);
            dto.setShippingStatus((String) result[18]);
            dto.setTrackingNumber((String) result[19]);
            orderDetails.add(dto);
        }

        return orderDetails;
    }
}

