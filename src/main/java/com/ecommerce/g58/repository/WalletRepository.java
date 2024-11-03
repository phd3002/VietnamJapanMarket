package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT w.balance FROM Wallet w WHERE w.userId.username = :username")
    Double findBalanceByUsername(String username);
}
