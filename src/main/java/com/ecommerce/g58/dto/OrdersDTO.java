package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.text.NumberFormat;
import java.time.LocalDateTime;

@Setter
@Getter
public class OrdersDTO {
    private int orderId;
    private LocalDateTime orderDate;
    private String status;
    private Integer totalQuantity;
    private Long totalPrice;
    private String formattedPrice; // Formatted price

    // Add a method to format the price as VND
    public void setFormattedPrice(Long price) {
        if (price != null) {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            this.formattedPrice = currencyFormatter.format(price).replace("₫", "VND");
        }
    }
}