package com.ecommerce.g58.utils;

import com.ecommerce.g58.entity.Orders;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Orders> hasUserId(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId").get("userId"), userId);
    }

    public static Specification<Orders> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("shippingStatus").get("status"), status);
        };
    }

    public static Specification<Orders> hasOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get("orderDate"), startDate, endDate);
        };
    }
}
