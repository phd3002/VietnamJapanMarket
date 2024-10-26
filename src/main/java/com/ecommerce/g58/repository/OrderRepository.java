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

    List<Orders> findByUserId(Users user);

    // Add new methods to handle shipping address
    @Query("SELECT o.shippingAddress FROM Orders o WHERE o.orderId = :orderId")
    String findShippingAddressByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.shippingAddress = :newAddress WHERE o.orderId = :orderId")
    void updateShippingAddressByOrderId(@Param("orderId") Integer orderId, @Param("newAddress") String newAddress);

    @Query(value = "SELECT \n" +
            "    o.order_id AS orderId, \n" +
            "    o.order_date AS orderDate, \n" +
            "    latest_status.status AS status, \n" +
            "    SUM(od.quantity) AS totalQuantity, \n" +
            "    (o.total_price + i.shipping_fee) AS totalPrice \n" +
            "FROM \n" +
            "    orders o \n" +
            "JOIN \n" +
            "    order_details od ON o.order_id = od.order_id \n" +
            "JOIN \n" +
            "    invoice i ON o.order_id = i.order_id \n" +
            "JOIN (\n" +
            "    SELECT ss.order_id, ss.status\n" +
            "    FROM shipping_status ss\n" +
            "    INNER JOIN (\n" +
            "        SELECT order_id, MAX(updated_at) as latest_update\n" +
            "        FROM shipping_status\n" +
            "        GROUP BY order_id\n" +
            "    ) latest ON ss.order_id = latest.order_id AND ss.updated_at = latest.latest_update\n" +
            ") latest_status ON o.order_id = latest_status.order_id \n" +
            "WHERE o.user_id = :userId " +
            "AND (COALESCE(:status, '') = '' OR latest_status.status = :status)\n" +
            "GROUP BY \n" +
            "    o.order_id, o.order_date, latest_status.status, o.total_price, i.shipping_fee",
            countQuery = "SELECT COUNT(DISTINCT o.order_id) " +
                    "FROM orders o " +
                    "JOIN (\n" +
                    "    SELECT ss.order_id, ss.status\n" +
                    "    FROM shipping_status ss\n" +
                    "    INNER JOIN (\n" +
                    "        SELECT order_id, MAX(updated_at) as latest_update\n" +
                    "        FROM shipping_status\n" +
                    "        GROUP BY order_id\n" +
                    "    ) latest ON ss.order_id = latest.order_id AND ss.updated_at = latest.latest_update\n" +
                    ") latest_status ON o.order_id = latest_status.order_id " +
                    "WHERE o.user_id = :userId " +
                    "AND (COALESCE(:status, '') = '' OR latest_status.status = :status)",
            nativeQuery = true)
    Page<Object[]> findOrdersByUserIdAndStatus(@Param("userId") Integer userId,
                                               @Param("status") String status,
                                               Pageable pageable);
}
