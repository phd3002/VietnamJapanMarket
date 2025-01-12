package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wallet;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WalletService {

    Page<WalletDTO> getTransactionsForUserId(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    Page<WalletDTO> getWithdrawRequest(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    List<WalletDTO> getTransactionsForDataTable(Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    // lay ra so du cua user
    long getUserWalletBalance(Integer userId);

    // tru tien tu vi
    void deductFromWallet(Integer userId, double amount, String paymentType);

    // them tien vao vi
    void addToWallet(Integer userId, double amount, String paymentType);

    void createWalletForUser(Users userId, long initialBalance);

    void recharge(Integer amount);

    void addToWalletForAdmin(double amount, String paymentType, Orders orders);
    void withdraw(Wallet wallet, String bankName, String bankAccount, BigDecimal amount);
}
