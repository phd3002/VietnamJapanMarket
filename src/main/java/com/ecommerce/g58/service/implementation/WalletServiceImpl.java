package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wallet;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.TransactionRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.repository.WalletRepository;
import com.ecommerce.g58.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ecommerce.g58.utils.FormatVND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    @Override
    public long getUserWalletBalance(Integer userId) {
        // Lấy số dư ví của người dùng từ walletRepository
        return walletRepository.findBalanceByUserId(userId).orElse(0L);
    }

    @Override
    public void deductFromWallet(Integer userId, double amount, String paymentType) {
        // Retrieve user entity
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        Users user = optionalUser.get();

        // Retrieve user's wallet entity
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(user);
        if (optionalWallet.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
        }
        Wallet wallet = optionalWallet.get();

        // Check if wallet balance is sufficient
        Long currentBalance = wallet.getBalance();
        Long amountToDeduct = (long) amount;

        if (currentBalance < amountToDeduct) {
            throw new IllegalArgumentException("Số dư trong ví không đủ");
        }

        // Deduct balance and save updated wallet
        Long newBalance = currentBalance - amountToDeduct;
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Record transaction
        Transactions transaction = new Transactions();
        transaction.setFromWalletId(wallet);
        transaction.setAmount(-amountToDeduct);
        transaction.setTransactionType("Trừ tiền");
        transaction.setDescription(paymentType.equals("deposit") ? "Đặt cọc 50% khi mua hàng" : "Thanh toán đầy đủ khi mua hàng");
        transaction.setPaymentType(paymentType); // Set payment type
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void addToWallet(Integer userId, double amount, String paymentType) {
        // Retrieve user entity
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        Users user = optionalUser.get();

        // Retrieve user's wallet entity
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(user);
        if (optionalWallet.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
        }
        Wallet wallet = optionalWallet.get();

        // Add balance and save updated wallet
        Long currentBalance = wallet.getBalance();
        Long amountToAdd = (long) amount;
        Long newBalance = currentBalance + amountToAdd;
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Record transaction
        Transactions transaction = new Transactions();
        transaction.setToWalletId(wallet);
        transaction.setAmount(amountToAdd);
        transaction.setTransactionType("Cộng tiền");
        transaction.setDescription(paymentType.equals("deposit") ? "Người mua đặt cọc 50% khi mua hàng" : "Người mua thanh toán đầy đủ khi mua hàng");
        transaction.setPaymentType(paymentType); // Set payment type
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

//    @Override
//    public void addToWalletForLogistics(Long amount, int orderId) {
//        Orders order = orderRepository.findOrdersByOrderId(orderId);
//        Optional<Wallet> userWallet = walletRepository.findByUserId(order.getUserId());
//
//        // Retrieve user's wallet entity
//        Optional<Wallet> optionalWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5);
//        if (optionalWallet.isEmpty()) {
//            throw new IllegalArgumentException("Không tìm thấy ví của người dùng");
//        }
//        Wallet wallet = optionalWallet.get();
//
//        // Add balance and save updated wallet
//        Long currentBalance = wallet.getBalance();
//        Long newBalance = currentBalance + amount;
//        wallet.setBalance(newBalance);
//        walletRepository.save(wallet);
//
//        // Record transaction
//        Transactions transaction = new Transactions();
//        transaction.setToWalletId(wallet);
//        transaction.setAmount(amount);
//        userWallet.ifPresent(transaction::setFromWalletId);
//        transaction.setTransactionType("Cộng tiền");
//        transaction.setDescription("Nhận tiền ship cho đơn hàng " + order.getOrderCode());
//        transaction.setCreatedAt(LocalDateTime.now());
//
//        transactionRepository.save(transaction);
//    }


    public void createWalletForUser(Users userId, long initialBalance) {
        Wallet wallet = new Wallet(); // Tạo một đối tượng Wallet mới
        wallet.setUserId(userId); // Đặt userId cho ví mới
        wallet.setBalance(initialBalance); // Đặt số dư ban đầu cho ví (0)
        wallet.setLastUpdated(LocalDateTime.now()); // Cập nhật thời gian cập nhật cuối cùng

        walletRepository.save(wallet); // Lưu ví mới vào cơ sở dữ liệu
    }

    @Override
    public void recharge(Integer amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        Users user = userRepository.findByEmail(authentication.getName());

        Optional<Wallet> userWallet = walletRepository.findByUserId(user);
        if (userWallet.isPresent()) {
            Wallet wallet = userWallet.get();
            wallet.setBalance(amount + wallet.getBalance());
            wallet.setLastUpdated(LocalDateTime.now());
            walletRepository.save(wallet);

            Transactions transactions = new Transactions();
            transactions.setAmount(amount);
            transactions.setToWalletId(userWallet.get());
            transactions.setTransactionType("Nạp tiền");
            transactions.setDescription("Nạp thành công " + FormatVND.formatCurrency(BigDecimal.valueOf(amount)) + " vào tài khoản của bạn ");
            transactions.setCreatedAt(LocalDateTime.now());
            transactionRepository.save(transactions);
        }
    }
    @Override
    public Page<WalletDTO> getTransactionsForUserId(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // Create a Pageable object
        PageRequest pageable = PageRequest.of(page, size);

        // Fetch transactions with pagination and date filtering
        Page<Object[]> results = walletRepository.findTransactionsForUserId(userId, startDate, endDate, pageable);

        // Map the results to WalletDTO
        Page<WalletDTO> walletPage = results.map(result -> {
            WalletDTO dto = new WalletDTO();

            // Set transaction date
            Timestamp transactionDateTimestamp = (Timestamp) result[0];
            dto.setTransactionDate(transactionDateTimestamp.toLocalDateTime());

            // Set transaction type
            String transactionType = (String) result[1];
            dto.setTransactionType(transactionType);

            // Set amount
            if (result[2] instanceof BigInteger) {
                dto.setAmount(new BigDecimal((BigInteger) result[2]));
            } else if (result[2] instanceof BigDecimal) {
                dto.setAmount((BigDecimal) result[2]);
            }

            // Safely retrieve paymentType (checking if it exists)
            String paymentType = (result.length > 6 && result[6] != null) ? (String) result[6] : null;

            // Set description based on transaction type and paymentType
            if ("deposit".equalsIgnoreCase(paymentType)) {
                if ("ADD".equalsIgnoreCase(transactionType.trim()) || "Cộng tiền".equals(transactionType.trim())) {
                    dto.setDescription("Người mua đặt cọc 50% khi mua hàng");
                } else if ("DEDUCT".equalsIgnoreCase(transactionType.trim()) || "Trừ tiền".equals(transactionType.trim())) {
                    dto.setDescription("Đặt cọc 50% khi mua hàng");
                }
            } else if ("full".equalsIgnoreCase(paymentType)) {
                // For full payment transactions
                if ("ADD".equalsIgnoreCase(transactionType.trim()) || "Cộng tiền".equals(transactionType.trim())) {
                    dto.setDescription("Người mua thanh toán đầy đủ khi mua hàng");
                } else if ("DEDUCT".equalsIgnoreCase(transactionType.trim()) || "Trừ tiền".equals(transactionType.trim())) {
                    dto.setDescription("Thanh toán đầy đủ khi mua hàng");
                }
            } else {
                dto.setDescription((String) result[3]); // Default description for other types
            }

            // Set transactionParty based on transaction type
            if ("Trừ tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã gửi cho shop");
            } else if ("Cộng tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã nhận từ người mua");
            } else {
                dto.setTransactionParty("Unknown"); // Fallback, if needed
            }

            // Set wallet balance
            if (result[5] instanceof BigInteger) {
                dto.setWalletBalance(new BigDecimal((BigInteger) result[5]));
            } else if (result[5] instanceof BigDecimal) {
                dto.setWalletBalance((BigDecimal) result[5]);
            }

            return dto;
        });

        return walletPage;
    }
}
