package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.repository.WalletRepository;
import com.ecommerce.g58.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Double getBalanceByUsername(String username) {
        return walletRepository.findBalanceByUsername(username);
    }
}