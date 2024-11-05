package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    void deleteByUserId_UserId(Integer userId);
    @Query(value = "SELECT \n" +
            "                t.created_at as transactionDate,\n" +
            "                t.transaction_type as transactionType,\n" +
            "                CASE \n" +
            "                    WHEN w1.user_id = :userId THEN -t.amount\n" +
            "                    WHEN w2.user_id = :userId THEN +t.amount\n" +
            "                END as amountChange,\n" +
            "                t.description,\n" +
            "                CASE \n" +
            "                    WHEN w1.user_id = :userId THEN CONCAT('Đã gửi cho ', COALESCE(receiver.username, 'Unknown'))\n" +
            "                    WHEN w2.user_id = :userId THEN CONCAT('Đã nhận từ ', COALESCE(sender.username, 'Unknown'))\n" +
            "                    ELSE 'Unknown transaction'\n" +
            "                END as transactionParty,\n" +
            "                CASE \n" +
            "                    WHEN w1.user_id = :userId THEN w1.balance\n" +
            "                    WHEN w2.user_id = :userId THEN w2.balance\n" +
            "                END as walletBalance\n" +
            "            FROM transactions t\n" +
            "            LEFT JOIN wallet w1 ON t.from_wallet_id = w1.wallet_id\n" +
            "            LEFT JOIN wallet w2 ON t.to_wallet_id = w2.wallet_id\n" +
            "            LEFT JOIN users sender ON w1.user_id = sender.user_id\n" +
            "            LEFT JOIN users receiver ON w2.user_id = receiver.user_id\n" +
            "            WHERE w1.user_id = :userId OR w2.user_id = :userId\n" +
            "            ORDER BY t.created_at DESC",
            nativeQuery = true)
    List<Object[]> findTransactionsForUserId(@Param("userId") Integer userId);

    Optional<Wallet> findByUserId(Integer userId);
}