package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingStatusRepository extends JpaRepository<ShippingStatus, Integer> {
    ShippingStatus findByOrderId(Integer orderId);
    ShippingStatus findByOrderIdOrderId(Integer orderId);
}