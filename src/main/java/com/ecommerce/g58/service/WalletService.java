package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<WalletDTO> getTransactionsForUserId(Integer userId);
    void deposit(Integer userId, BigDecimal amount, String bankName, String accountNumber);
    void withdraw(Integer userId, BigDecimal amount, String bankName, String accountNumber);
}
