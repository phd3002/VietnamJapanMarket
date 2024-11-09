package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderManagerDTO {
    private Integer orderId;
    private String customerName;
    private String productNames;
    private Integer totalProducts;
    private Integer totalPrice;
    private String latestStatus;
}