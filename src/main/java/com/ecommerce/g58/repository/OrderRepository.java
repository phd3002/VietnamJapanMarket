package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query(value = "SELECT o.order_id AS orderId, o.order_date AS orderDate, ss.status AS status, " +
            "SUM(od.quantity) AS totalQuantity, o.total_price AS totalPrice " +
            "FROM orders o " +
            "JOIN order_details od ON o.order_id = od.order_id " +
            "JOIN shipping_status ss ON o.order_id = ss.order_id " +
            "GROUP BY o.order_id, o.order_date, ss.status, o.total_price",
            nativeQuery = true)
    List<Object[]> findOrderSummaries();
}
