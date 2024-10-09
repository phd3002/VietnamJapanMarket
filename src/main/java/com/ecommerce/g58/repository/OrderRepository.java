package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT new com.ecommerce.g58.dto.OrdersDTO(o.orderId, o.orderDate, ss.status, od.quantity, o.totalPrice) " +
            "FROM Orders o " +
            "JOIN o.orderDetails od " +
            "JOIN o.shippingStatus ss")
    List<OrdersDTO> findOrderDetails();
}
