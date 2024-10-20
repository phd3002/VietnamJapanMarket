package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.OrdersDTO;
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

    @Query(value = "SELECT \n" +
            "    o.order_id AS orderId, \n" +
            "    o.order_date AS orderDate, \n" +
            "    ss.status AS status, \n" +
            "    SUM(od.quantity) AS totalQuantity, \n" +
            "    (o.total_price + i.shipping_fee) AS totalPrice \n" +
            "FROM \n" +
            "    orders o \n" +
            "JOIN \n" +
            "    order_details od ON o.order_id = od.order_id \n" +
            "JOIN \n" +
            "    shipping_status ss ON o.order_id = ss.order_id \n" +
            "JOIN \n" +
            "    invoice i ON o.order_id = i.order_id \n" +
            "WHERE o.user_id = :userId " +
            "GROUP BY \n" +
            "    o.order_id, o.order_date, ss.status, o.total_price, i.shipping_fee",
            nativeQuery = true)
    List<Object[]> findOrdersByUserId(@Param("userId") Integer userId);

    List<Orders> findByUserId(Users user);

    // Add new methods to handle shipping address
    @Query("SELECT o.shippingAddress FROM Orders o WHERE o.orderId = :orderId")
    String findShippingAddressByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.shippingAddress = :newAddress WHERE o.orderId = :orderId")
    void updateShippingAddressByOrderId(@Param("orderId") Integer orderId, @Param("newAddress") String newAddress);
}
