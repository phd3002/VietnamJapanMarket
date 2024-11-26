package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDetailManagerDTO {
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private String orderStatus;
    private String orderCode;
    private LocalDateTime statusTime;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private LocalDateTime orderDate;
    private String productName;
    private String productType;
    private Integer quantity;
    private Integer productPrice;
    private Integer totalPrice;
    private Integer shippingFee;
    private Integer tax;
    private String paymentMethod;
    private Integer totalAmount;
}
