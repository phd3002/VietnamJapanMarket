package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wallet;
import com.ecommerce.g58.repository.TransactionRepository;
import com.ecommerce.g58.repository.UserRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

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
        // Lấy số dư ví của người dùng từ walletRepository
        return walletRepository.findBalanceByUserId(userId).orElse(0L);
    }

    @Override
    public void deductFromWallet(Integer userId, double amount) {

        // Lấy thực thể người dùng từ userRepository
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        Users user = optionalUser.get();

        // Lấy thực thể ví của người dùng từ walletRepository
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(user);
        if (optionalWallet.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
        }
        Wallet wallet = optionalWallet.get();

        // Kiểm tra nếu số dư ví đủ để thực hiện thanh toán
        Long currentBalance = wallet.getBalance();
        Long amountToDeduct = (long) amount; // Chuyển đổi amount thành Long

        if (currentBalance < amountToDeduct) {
            throw new IllegalArgumentException("Số dư trong ví không đủ");
        }

        // Trừ số dư và lưu lại
        Long newBalance = currentBalance - amountToDeduct;
        wallet.setBalance(newBalance);

        // Lưu lại ví đã cập nhật
        walletRepository.save(wallet);

        // Ghi lại giao dịch
        Transactions transaction = new Transactions();
        transaction.setFromWalletId(wallet); // Lấy ví mà số tiền bị trừ
        transaction.setAmount(-amountToDeduct); // Đặt giá trị âm để thể hiện trừ tiền
        transaction.setTransactionType("Trừ tiền"); // Thiết lập loại giao dịch
        transaction.setDescription("Thanh toán khi mua hàng");
        transaction.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian giao dịch hiện tại

        // Lưu lại giao dịch vào repository
        transactionRepository.save(transaction);
    }

    @Override
    public void addToWallet(Integer userId, double amount) {
        // Lấy thực thể người dùng từ userRepository
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        Users user = optionalUser.get();

        // Lấy thực thể ví của người dùng từ walletRepository
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(user);
        if (optionalWallet.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
        }
        Wallet wallet = optionalWallet.get();

        // Lấy số dư hiện tại và cộng thêm số tiền
        Long currentBalance = wallet.getBalance();
        Long amountToAdd = (long) amount; // Chuyển đổi amount thành Long

        // Cộng số dư và lưu lại
        Long newBalance = currentBalance + amountToAdd;
        wallet.setBalance(newBalance);

        // Lưu lại ví đã cập nhật
        walletRepository.save(wallet);

        // Ghi lại giao dịch
        Transactions transaction = new Transactions();
        transaction.setToWalletId(wallet); // Lấy ví mà số tiền được cộng vào
        transaction.setAmount(amountToAdd); // Đặt giá trị dương để thể hiện cộng tiền
        transaction.setTransactionType("Cộng tiền"); // Thiết lập loại giao dịch
        transaction.setDescription("Nhận tiền khi bán hàng");
        transaction.setCreatedAt(LocalDateTime.now()); // Thiết lập thời gian giao dịch hiện tại

        // Lưu lại giao dịch vào repository
        transactionRepository.save(transaction);
    }


}
