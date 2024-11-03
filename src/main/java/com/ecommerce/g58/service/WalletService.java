package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<WalletDTO> getTransactionsForUserId(Integer userId);
}
