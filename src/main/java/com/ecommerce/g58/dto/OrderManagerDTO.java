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
    private String reason;
    private String previous_status;

    public OrderManagerDTO() {
    }

    public OrderManagerDTO(Integer orderId, String customerName, String productNames, Integer totalProducts, Integer totalPrice, String latestStatus, String reason, String previous_status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productNames = productNames;
        this.totalProducts = totalProducts;
        this.totalPrice = totalPrice;
        this.latestStatus = latestStatus;
        this.reason = reason;
        this.previous_status = previous_status;
    }
}