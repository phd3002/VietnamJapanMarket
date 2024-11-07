package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<WalletDTO> getTransactionsForUserId(Integer userId);

    long getUserWalletBalance(Integer userId);

    void deductFromWallet(Integer userId, double amount);
}
