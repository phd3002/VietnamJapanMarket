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
    private Integer totalQuantity;
    private Long totalPrice;
}