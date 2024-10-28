package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.userId.userId = :userId")
    List<Orders> findByUserId(Users user);

    // Add new methods to handle shipping address
    @Query("SELECT o.shippingAddress FROM Orders o WHERE o.orderId = :orderId")
    String findShippingAddressByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.shippingAddress = :newAddress WHERE o.orderId = :orderId")
    void updateShippingAddressByOrderId(@Param("orderId") Integer orderId, @Param("newAddress") String newAddress);

    @Query(value = "SELECT " +
            "    o.order_id AS orderId, " +
            "    o.order_date AS orderDate, " +
            "    COALESCE(ss.status, '') AS status, " +
            "    SUM(od.quantity) AS totalQuantity, " +
            "    (o.total_price + i.shipping_fee) AS totalPrice " +
            "FROM orders o " +
            "LEFT JOIN order_details od ON o.order_id = od.order_id " +
            "LEFT JOIN shipping_status ss ON o.order_id = ss.order_id " +
            "LEFT JOIN invoice i ON o.order_id = i.order_id " +
            "WHERE o.user_id = :userId " +
            "AND (:status IS NULL OR :status = '' OR ss.status = :status) " +
            "GROUP BY o.order_id, o.order_date, ss.status, o.total_price, i.shipping_fee",
            countQuery = "SELECT COUNT(DISTINCT o.order_id) " +
                    "FROM orders o " +
                    "LEFT JOIN shipping_status ss ON o.order_id = ss.order_id " +
                    "WHERE o.user_id = :userId " +
                    "AND (:status IS NULL OR :status = '' OR ss.status = :status)",
            nativeQuery = true)
    Page<Object[]> findOrdersByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status, Pageable pageable);

}
