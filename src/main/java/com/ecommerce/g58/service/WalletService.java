package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Users;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    // lay ra tat ca cac giao dich cua user
    List<WalletDTO> getTransactionsForUserId(Integer userId);

    // lay ra so du cua user
    long getUserWalletBalance(Integer userId);

    // tru tien tu vi
    void deductFromWallet(Integer userId, double amount, String paymentType);

    // them tien vao vi
    void addToWallet(Integer userId, double amount, String paymentType);

    void createWalletForUser(Users userId, long initialBalance);

//    void addToWalletForLogistics( Long amount, int orderId);
}
