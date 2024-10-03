package com.ecommerce.g58.dto;

import java.util.List;

public class OrderDetailsDTO {
    private Long orderId;
    private String date;
    private String status;
    private String address;
    private List<OrderItemDTO> orderItems;
    private Double totalPrice;
    private Double additionalFee;
    private Double commissionFee;
    private Double shippingFee;
    private Double discountShippingFee;
    private Double voucherFromShop;
    private Double totalAmount;

    // Nested DTO for Order Items
    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private Double price;

        // Getters and Setters
    }

    // Getters and Setters
}
