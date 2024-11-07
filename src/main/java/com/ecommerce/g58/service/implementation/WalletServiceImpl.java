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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    public long getUserWalletBalance(Integer userId) {
        return walletRepository.findBalanceByUserId(userId).orElse(0L);
    }

    @Override
    public void deductFromWallet(Integer userId, double amount) {
        // Lấy thực thể ví của người dùng từ walletRepository
        Optional<Wallet> optionalWallet = walletRepository.findById(userId);
        if (!optionalWallet.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
        }

        Wallet wallet = optionalWallet.get();

        // Kiểm tra nếu số dư ví đủ để thực hiện thanh toán
        Long currentBalance = wallet.getBalance();
        if (currentBalance < amount) {
            throw new IllegalArgumentException("Số dư trong ví không đủ");
        }

        // Trừ số dư và lưu lại
        Long newBalance = currentBalance - (long) amount;
        wallet.setBalance(newBalance);

        // Lưu lại ví đã cập nhật
        walletRepository.save(wallet);

        // Ghi lại giao dịch
        Transactions transaction = new Transactions();
        transaction.setFromWalletId(wallet); // Lấy ví mà số tiền bị trừ
        transaction.setAmount(BigDecimal.valueOf(amount).negate()); // Chuyển đổi sang BigDecimal và đặt giá trị âm
        transaction.setTransactionType("DEDUCT"); // Thiết lập loại giao dịch
        transaction.setDescription("Thanh toán khi mua hàng");
        transaction.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian giao dịch hiện tại

        // Lưu lại giao dịch vào repository
        transactionRepository.save(transaction);
    }
}
