package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Wallet;
import com.ecommerce.g58.repository.TransactionRepository;
import com.ecommerce.g58.repository.WalletRepository;
import com.ecommerce.g58.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public List<WalletDTO> getTransactionsForUserId(Integer userId) {
        List<Object[]> results = walletRepository.findTransactionsForUserId(userId);
        List<WalletDTO> transactions = new ArrayList<>();
        for (Object[] result : results) {
            WalletDTO dto = new WalletDTO();
            Timestamp transactionDateTimestamp = (Timestamp) result[0];
            dto.setTransactionDate(transactionDateTimestamp.toLocalDateTime());
            dto.setTransactionType((String) result[1]);

            if (result[2] instanceof BigInteger) {
                dto.setAmount(new BigDecimal((BigInteger) result[2]));
            } else if (result[2] instanceof BigDecimal) {
                dto.setAmount((BigDecimal) result[2]);
            }

            dto.setDescription((String) result[3]);
            dto.setTransactionParty((String) result[4]);

            if (result[5] instanceof BigInteger) {
                dto.setWalletBalance(new BigDecimal((BigInteger) result[5]));
            } else if (result[5] instanceof BigDecimal) {
                dto.setWalletBalance((BigDecimal) result[5]);
            }
            transactions.add(dto);
        }
        return transactions;
    }

    @Override
    public void deposit(Integer userId, BigDecimal amount, String bankName, String accountNumber) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // Lưu giao dịch nạp tiền
        Transactions transaction = new Transactions();
        transaction.setToWalletId(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("Nạp tiền");
        transaction.setDescription("Nạp tiền từ " + bankName + " - " + accountNumber);
        transaction.setIsRefund("No");

        transactionRepository.save(transaction);
    }

    @Override
    public void withdraw(Integer userId, BigDecimal amount, String bankName, String accountNumber) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Không đủ số dư để rút tiền");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Lưu giao dịch rút tiền
        Transactions transaction = new Transactions();
        transaction.setFromWalletId(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("Rút tiền");
        transaction.setDescription("Rút tiền vào " + bankName + " - " + accountNumber);
        transaction.setIsRefund("No");

        transactionRepository.save(transaction);
    }

}
