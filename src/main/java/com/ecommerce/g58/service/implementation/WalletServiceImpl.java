package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.WalletDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wallet;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.TransactionRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.repository.WalletRepository;
import com.ecommerce.g58.service.EmailService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private EmailService emailService;

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
        transaction.setPreviousBalance(newBalance);
        transaction.setTransactionType("Trừ tiền");
        transaction.setDescription(paymentType.equals("deposit") ? "Đặt cọc 50% khi mua hàng" : "Thanh toán đầy đủ khi mua hàng");
        transaction.setPaymentType(paymentType); // Set payment type
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
        emailService.sendTransactionMailAsync(user, transaction, amountToDeduct);
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

    @Override
    public void addToWalletForAdmin(double amount, String paymentType, Orders orders) {
        // Retrieve user entity
        Users adminUser = userRepository.findFirstByRoleId_RoleId(1);



        // Retrieve user's wallet entity
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(adminUser);
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
        transaction.setPreviousBalance(newBalance);
        transaction.setTransactionType("Cộng tiền");
        transaction.setDescription(paymentType.equals("deposit") ? "Người mua đặt cọc 50% khi mua hàng cho đơn hàng "+ orders.getOrderCode() : "Người mua thanh toán đầy đủ khi mua hàng cho đơn hàng : "+ orders.getOrderCode());
        transaction.setPaymentType(paymentType); // Set payment type
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void withdraw(Wallet wallet, String bankName, String bankAccount, BigDecimal amount) {
        Transactions transactions = new Transactions();
        transactions.setToWalletId(wallet);
        transactions.setTransactionType("Rút tiền");
        transactions.setAmount(amount.negate().longValue());
        transactions.setPreviousBalance(wallet.getBalance());
        transactions.setDescription("Yêu cầu rút tiền về tài khoản <strong> "+bankAccount+" </strong> - Ngân hàng <strong>" +bankName+" </strong>");
        transactions.setStatus(String.valueOf(2)); // 2 là đang xử lý
        transactions.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transactions);
        logger.info("Thêm transaction rút tiền thành công");
//        BigDecimal currentBalance = BigDecimal.valueOf(wallet.getBalance());
//        BigDecimal newBalance = currentBalance.subtract(amount);
//        wallet.setBalance(newBalance.longValue());
//        walletRepository.save(wallet);

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
            transactions.setPreviousBalance(wallet.getBalance() - amount);
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
            if (result[3] instanceof BigInteger) {
                dto.setPreviousBalance(new BigDecimal((BigInteger) result[3]));
            } else if (result[3] instanceof BigDecimal) {
                dto.setPreviousBalance((BigDecimal) result[3]);
            }
                dto.setDescription((String) result[4]); // Default description for other types


            // Set transactionParty based on transaction type
            if ("Trừ tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã gửi cho shop");
            } else if ("Cộng tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã nhận từ người mua");
            } else {
                dto.setTransactionParty(" "); // Fallback, if needed
            }

            // Set wallet balance
            if (result[6] instanceof BigInteger) {
                dto.setWalletBalance(new BigDecimal((BigInteger) result[6]));
            } else if (result[6] instanceof BigDecimal) {
                dto.setWalletBalance((BigDecimal) result[6]);
            }
            if (result[8] != null) {
                if (result[8] instanceof Integer) {
                    dto.setStatus((Integer) result[8]);
                } else if (result[8] instanceof Number) {
                    dto.setStatus(((Number) result[8]).intValue());
                } else {
                    dto.setStatus(1); // hoặc một giá trị mặc định nào đó
                }
            } else {
                dto.setStatus(1); // hoặc một giá trị mặc định nào đó
            }
            return dto;
        });

        return walletPage;
    }

    @Override
    public Page<WalletDTO> getWithdrawRequest(Integer userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // Create a Pageable object
        PageRequest pageable = PageRequest.of(page, size);

        // Fetch transactions with pagination and date filtering
        Page<Object[]> results = walletRepository.findWithdrawRequest(userId, startDate, endDate, pageable);

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
            if (result[3] instanceof BigInteger) {
                dto.setPreviousBalance(new BigDecimal((BigInteger) result[3]));
            } else if (result[3] instanceof BigDecimal) {
                dto.setPreviousBalance((BigDecimal) result[3]);
            }
            dto.setDescription((String) result[4]); // Default description for other types


            // Set transactionParty based on transaction type
            if ("Trừ tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã gửi cho shop");
            } else if ("Cộng tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã nhận từ người mua");
            } else {
                dto.setTransactionParty(" "); // Fallback, if needed
            }

            // Set wallet balance
            if (result[6] instanceof BigInteger) {
                dto.setWalletBalance(new BigDecimal((BigInteger) result[6]));
            } else if (result[6] instanceof BigDecimal) {
                dto.setWalletBalance((BigDecimal) result[6]);
            }
            if (result[8] != null) {
                if (result[8] instanceof Integer) {
                    dto.setStatus((Integer) result[8]);
                } else if (result[8] instanceof Number) {
                    dto.setStatus(((Number) result[8]).intValue());
                } else {
                    dto.setStatus(1); // hoặc một giá trị mặc định nào đó
                }
            } else {
                dto.setStatus(1); // hoặc một giá trị mặc định nào đó
            }
            if (result[9] != null) {
                if (result[9] instanceof Integer) {
                    dto.setId((Integer) result[9]);
                } else if (result[9] instanceof Number) {
                    dto.setId(((Number) result[9]).intValue());
                } else {
                    dto.setId(1); // hoặc một giá trị mặc định nào đó
                }
            } else {
                dto.setId(1); // hoặc một giá trị mặc định nào đó
            }
            return dto;
        });

        return walletPage;
    }

    @Override
    public List<WalletDTO> getTransactionsForDataTable(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {

        // Fetch transactions with filtering for date range
        List<Object[]> results = walletRepository.findTransactionsForDataTable(userId, startDate, endDate);

        // Map the results to WalletDTO
        List<WalletDTO> walletList = results.stream().map(result -> {
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

            // Set previous balance
            if (result[3] instanceof BigInteger) {
                dto.setPreviousBalance(new BigDecimal((BigInteger) result[3]));
            } else if (result[3] instanceof BigDecimal) {
                dto.setPreviousBalance((BigDecimal) result[3]);
            }

            // Set description
            dto.setDescription((String) result[4]);

            // Set transactionParty based on transaction type
            if ("Trừ tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã gửi cho shop");
            } else if ("Cộng tiền".equals(transactionType)) {
                dto.setTransactionParty("Đã nhận từ người mua");
            } else {
                dto.setTransactionParty(""); // Fallback, if needed
            }

            // Set wallet balance
            if (result[6] instanceof BigInteger) {
                dto.setWalletBalance(new BigDecimal((BigInteger) result[6]));
            } else if (result[6] instanceof BigDecimal) {
                dto.setWalletBalance((BigDecimal) result[6]);
            }
            if (result[8] != null) {
                if (result[8] instanceof Integer) {
                    dto.setStatus((Integer) result[8]);
                } else if (result[8] instanceof Number) {
                    dto.setStatus(((Number) result[8]).intValue());
                } else {
                    dto.setStatus(1); // hoặc một giá trị mặc định nào đó
                }
            } else {
                dto.setStatus(1); // hoặc một giá trị mặc định nào đó
            }
            return dto;
        }).collect(Collectors.toList()); // Use Collectors.toList() for Java 8 compatibility

        return walletList;
    }
}
