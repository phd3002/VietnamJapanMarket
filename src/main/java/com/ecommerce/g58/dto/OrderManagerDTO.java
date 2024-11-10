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

    public OrderManagerDTO() {
    }

    public OrderManagerDTO(Integer orderId, String customerName, String productNames, Integer totalProducts, Integer totalPrice, String latestStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productNames = productNames;
        this.totalProducts = totalProducts;
        this.totalPrice = totalPrice;
        this.latestStatus = latestStatus;
    }
}