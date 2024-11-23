package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ShippingStatusRepository extends JpaRepository<ShippingStatus, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE shipping_status SET status = :status, updated_at = CURRENT_TIMESTAMP \n" +
            "WHERE order_id = :orderId", nativeQuery = true)
    void updateOrderStatus(@Param("orderId") Integer orderId, @Param("status") String status);

    ShippingStatus findShippingStatusByOrderId_OrderId(Integer id);
}