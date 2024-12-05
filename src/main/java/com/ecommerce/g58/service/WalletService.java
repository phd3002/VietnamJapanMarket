package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletService {

    Page<WalletDTO> getTransactionsForUserId(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    // lay ra so du cua user
    long getUserWalletBalance(Integer userId);

    // tru tien tu vi
    void deductFromWallet(Integer userId, double amount, String paymentType);

    // them tien vao vi
    void addToWallet(Integer userId, double amount, String paymentType);

    void createWalletForUser(Users userId, long initialBalance);

    void recharge(Integer amount);
}
