package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TotalOrderDTO {
    private BigDecimal totalOrder;
    private BigDecimal totalOrderCompleted;
    private BigDecimal totalOrderCancelledAndRefunded;
}
