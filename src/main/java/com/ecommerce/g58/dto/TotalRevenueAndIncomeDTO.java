package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TotalRevenueAndIncomeDTO {
    private String month;
    private BigDecimal totalRevenue;
    private BigDecimal totalIncome;
}
