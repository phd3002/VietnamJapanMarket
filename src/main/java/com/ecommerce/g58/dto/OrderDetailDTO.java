package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class OrderDetailDTO {
    private Long orderId;
    private String productName;
    private String productImage;
    private String categoryName;
    private String sizeAndColor;
    private BigDecimal orderTotalPrice;
    private BigDecimal productPrice;
    private Integer avgRating;
    private String storeName;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingStatus;
    private String trackingNumber;
}
