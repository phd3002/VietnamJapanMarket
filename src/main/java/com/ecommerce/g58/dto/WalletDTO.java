package com.ecommerce.g58.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private int id;
    private BigDecimal walletBalance;
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String transactionParty;
    private BigDecimal previousBalance;
    private int status;

    public WalletDTO(BigDecimal walletBalance, LocalDateTime transactionDate, String transactionType, BigDecimal amount, String description, String transactionParty, BigDecimal previousBalance, int status) {
        this.walletBalance = walletBalance;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionParty = transactionParty;
        this.previousBalance = previousBalance;
        this.status = status;
    }
}
