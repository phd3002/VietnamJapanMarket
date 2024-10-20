package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private Long orderId;
    private String productName;
    private String productImage;
    private String categoryName;
    private String sizeAndColor;
    private Integer orderTotalPrice;
    private Integer quantity;
    private Integer productTotalPrice;
    private Integer avgRating;
    private String storeName;
    private Integer totalAmount;
    private Integer shippingFee;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingStatus;
    private String trackingNumber;
}
