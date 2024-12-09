package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

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
    private LocalDate orderDate;
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
    private String formattedOrderDate;
    public String getFormattedOrderDate() {
        if (orderDate != null) {
            return orderDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }
        return "No Date";
    }

    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(totalPrice));
    }

}