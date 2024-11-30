package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShippingStatusRepository extends JpaRepository<ShippingStatus, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE shipping_status SET status = :status, updated_at = CURRENT_TIMESTAMP \n" +
            "WHERE order_id = :orderId", nativeQuery = true)
    void updateOrderStatus(@Param("orderId") Integer orderId, @Param("status") String status);

    ShippingStatus findShippingStatusByOrderId_OrderId(Integer id);
    ShippingStatus findByOrderId(Integer orderId);
    ShippingStatus findByOrderIdOrderId(Integer orderId);

    @Query("SELECT s FROM ShippingStatus s WHERE s.status = :status AND s.updatedAt <= :date")
    List<ShippingStatus> findByStatusAndUpdatedAtBefore(String status, LocalDateTime date);

}