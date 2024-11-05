package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDetailDTO {
    private Long orderId;
    private Long productId;
    private Long storeId;
    private String productName;
    private String productImage;
    private String categoryName;
    private String sizeAndColor;
    private Integer orderTotalPrice;
    private Integer quantity;
    private Integer productTotalPrice;
    private Integer avgRating;
    private String storeName;
    private String storeImage;
    private Integer totalAmount;
    private Integer shippingFee;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingStatus;
    private String trackingNumber;
    private LocalDateTime pendingTime;
    private LocalDateTime confirmedTime;
    private LocalDateTime processingTime;
    private LocalDateTime dispatchedTime;
    private LocalDateTime shippingTime;
    private LocalDateTime failedTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime completedTime;
    private LocalDateTime cancelledTime;
    private LocalDateTime returnedTime;
}
