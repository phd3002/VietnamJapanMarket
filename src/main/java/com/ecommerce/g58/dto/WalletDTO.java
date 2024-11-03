package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class WalletDTO {
    private BigDecimal walletBalance;
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String transactionParty;
}
