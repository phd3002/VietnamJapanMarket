package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class OrdersDTO {
    private int orderId;
    private LocalDateTime orderDate;
    private String status;
    private int quantity;
    private BigDecimal totalPrice;

    public OrdersDTO(int orderId, LocalDateTime orderDate, String status, int quantity, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

}