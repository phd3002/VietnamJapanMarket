package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

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
    private Integer deposit;
    private Integer remainingBalance;
    public String getFormattedTotalPrice() {
        return formatCurrency(BigDecimal.valueOf(totalPrice));
    }
    public String getFormattedProductPrice() {
        return formatCurrency(BigDecimal.valueOf(productPrice));
    }
    public String getFormattedTax() {
        return formatCurrency(BigDecimal.valueOf(tax));
    }
    public String getFormattedTotalAmount() {
        return formatCurrency(BigDecimal.valueOf(totalAmount));
    }
    public String getFormattedShippingFee() {
        return formatCurrency(BigDecimal.valueOf(shippingFee));
    }
    public String getFormattedDeposit() {
        return formatCurrency(BigDecimal.valueOf(deposit));
    }
    public String getFormattedRemainingBalance() {
        return formatCurrency(BigDecimal.valueOf(remainingBalance));
    }


}
