package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByWallet_WalletId(Integer walletId);
}
