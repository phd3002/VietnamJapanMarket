package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Orders> findByUserId(Users user);

    // Add new methods to handle shipping address
    @Query("SELECT o.shippingAddress FROM Orders o WHERE o.orderId = :orderId")
    String findShippingAddressByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.shippingAddress = :newAddress WHERE o.orderId = :orderId")
    void updateShippingAddressByOrderId(@Param("orderId") Integer orderId, @Param("newAddress") String newAddress);
}
