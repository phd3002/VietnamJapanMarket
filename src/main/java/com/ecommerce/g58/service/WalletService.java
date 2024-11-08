package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    // lay ra tat ca cac giao dich cua user
    List<WalletDTO> getTransactionsForUserId(Integer userId);

    // lay ra so du cua user
    long getUserWalletBalance(Integer userId);

    // tru tien tu vi
    void deductFromWallet(Integer userId, double amount);
}
