package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.enums.TransactionType;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.*;
import com.ecommerce.g58.utils.RandomOrderCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.g58.utils.OrderSpecification;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CartService cartService;
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public Page<OrdersDTO> getOrdersByUserIdAndStatusAndDate(Integer userId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Specification<Orders> spec = Specification.where(OrderSpecification.hasUserId(userId))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.hasOrderDateBetween(startDate, endDate));

        // Add sorting by orderId in descending order to pageable
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "orderId"));

        Page<Orders> ordersPage = orderRepository.findAll(spec, sortedPageable);

        // Mapping logic remains the same
        return ordersPage.map(order -> {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId(order.getOrderId());
            dto.setOrderCode(order.getOrderCode());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getShippingStatus()
                    .stream()
                    .max(Comparator.comparing(ShippingStatus::getUpdatedAt))
                    .map(ShippingStatus::getStatus)
                    .orElse(null));
            dto.setTotalQuantity(order.getOrderDetails()
                    .stream()
                    .mapToInt(OrderDetails::getQuantity)
                    .sum());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setFormattedPrice(order.getTotalPrice());
            return dto;
        });
    }


    @Override
    public List<OrderManagerDTO> getOrdersForStore(Integer userId) {
        List<Object[]> results = orderRepository.findOrdersByStoreUserId(userId);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO dto = new OrderManagerDTO();
            dto.setOrderId((Integer) result[0]);
            dto.setCustomerName((String) result[1]);
            dto.setProductNames((String) result[2]);
            if (result[3] instanceof BigDecimal) {
                dto.setTotalProducts(((BigDecimal) result[3]).intValue());
            } else if (result[3] instanceof Integer) {
                dto.setTotalProducts((Integer) result[3]);
            }
            if (result[4] instanceof BigDecimal) {
                dto.setTotalPrice(((BigDecimal) result[4]).intValue());
            } else if (result[4] instanceof Integer) {
                dto.setTotalPrice((Integer) result[4]);
            }
            dto.setLatestStatus((String) result[5]);
            orders.add(dto);
        }
        return orders;
    }

    @Override
    public List<OrderManagerDTO> getOrdersByStoreId(Integer storeId) {
        List<Object[]> results = orderRepository.findOrdersByStoreId(storeId);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId(result[0] != null ? ((Number) result[0]).intValue() : null); // orderId
            order.setCustomerName(result[1] != null ? result[1].toString() : ""); // customerName
            order.setProductNames(result[2] != null ? result[2].toString() : ""); // productNames
            order.setTotalProducts(result[3] != null ? ((Number) result[3]).intValue() : 0); // totalProducts
            order.setTotalPrice(result[4] != null ? ((Number) result[4]).intValue() : 0); // order_price
            order.setOrderDate(null); // Initialize orderDate

            // Correctly assign orderDate from result[5]
            if (result[5] != null) { // orderDate
                if (result[5] instanceof java.sql.Date) {
                    order.setOrderDate(((java.sql.Date) result[5]).toLocalDate());
                } else if (result[5] instanceof java.sql.Timestamp) {
                    order.setOrderDate(((java.sql.Timestamp) result[5]).toLocalDateTime().toLocalDate());
                } else if (result[5] instanceof LocalDate) {
                    order.setOrderDate((LocalDate) result[5]);
                } else {
                    order.setOrderDate(null);
                }
            }

            // Correctly assign latestStatus from result[6]
            order.setLatestStatus(result[6] != null ? result[6].toString() : "");

            // Assign latestReason from result[7]
            order.setReason(result[7] != null ? result[7].toString() : "");

            // Assign oldStatus from result[8]
            order.setPrevious_status(result[8] != null ? result[8].toString() : "");

            // Add to the list
            orders.add(order);
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(Orders orderId, String newStatus) {
        ShippingStatus status = new ShippingStatus();
        status.setOrderId(orderId);
        status.setStatus(newStatus);
        status.setUpdatedAt(LocalDateTime.now());
        shippingStatusRepository.save(status);
    }

    @Override
    public void updateOrderStatuss(Integer orderId, String status) {

        // Lấy thông tin đơn hàng và chi tiết đơn hàng
        Orders order = orderRepository.findOrdersByOrderId(orderId);
        OrderDetails details = orderDetailRepository.findByOrderId(orderId).get(0);

        Optional<Wallet> sellWallet = walletRepository.findByUserId(details.getProductId().getStoreId().getOwnerId());
        System.out.println("Sell Wallet: " + sellWallet);

        Invoice invoice = invoiceRepository.findInvoiceByOrderId(order);

        Users adminUser = userRepository.findFirstByRoleId_RoleId(1);
        Optional<Wallet> adminWallet = walletRepository.findByUserId(adminUser);
        System.out.println("Admin Wallet: " + adminWallet);
        Optional<Wallet> logisticWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5);
        System.out.println("Logistic Wallet: " + logisticWallet);

        // Xử lý theo trạng thái đơn hàng
        if (status.equalsIgnoreCase("Delivered")) {
            if (sellWallet.isPresent() && adminWallet.isPresent()) {
                if (invoice.getRemainingBalance().compareTo(BigDecimal.ZERO) != 0) {
                    // Cập nhật ví admin
                    BigDecimal currentAdminWallet = new BigDecimal(sellWallet.get().getBalance());
                    BigDecimal newSellerAmount = currentAdminWallet.add(invoice.getRemainingBalance());
                    sellWallet.get().setBalance(newSellerAmount.longValue());
                    walletRepository.save(adminWallet.get());

                    // Lưu giao dịch cho admin
                    Transactions sellerTransactions = new Transactions();
                    sellerTransactions.setCreatedAt(LocalDateTime.now());
                    sellerTransactions.setToWalletId(adminWallet.get());
                    sellerTransactions.setAmount(invoice.getRemainingBalance().abs().longValue());
                    sellerTransactions.setTransactionType("Thanh toán đơn hàng");
                    sellerTransactions.setDescription("Nhận " + invoice.getRemainingBalance() + " từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(sellerTransactions);


                    Transactions logisticTransactions = new Transactions();
                    logisticTransactions.setCreatedAt(LocalDateTime.now());
                    logisticTransactions.setToWalletId(logisticWallet.get());
                    logisticTransactions.setAmount(invoice.getRemainingBalance().abs().longValue());
                    logisticTransactions.setTransactionType("Nhận tiền hàng");
                    logisticTransactions.setDescription("Nhận " + invoice.getRemainingBalance() + " từ khách hàng ");
                    transactionRepository.save(logisticTransactions);


                    // Tạo thông báo
                    createNotification(sellWallet.get().getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " đã giao thành công. Số tiền nhận: " + invoice.getFormatedRemainingBalance(),
                            "/order-detail/" + orderId);
                    createNotification(order.getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " của bạn đã được giao thành công.",
                            "/order-detail/" + orderId);
                }
            }
            shippingStatusRepository.updateOrderStatus(orderId, status);
            System.out.println("Order status updated to " + status);
        } else if (status.equalsIgnoreCase("Failed")) {
            ShippingStatus shippingStatus = shippingStatusRepository.findShippingStatusByOrderId_OrderId(orderId);
            if (shippingStatus.getPrevious_status() == null) {
                shippingStatus.setPrevious_status("Fail");
                shippingStatus.setStatus("Shipping");
                shippingStatus.setUpdatedAt(LocalDateTime.now());
                shippingStatusRepository.save(shippingStatus);
            } else {
                shippingStatus.setStatus("Cancelled");
                shippingStatus.setPrevious_status("Failed");
                shippingStatus.setReason("Shipping failed");
                shippingStatus.setUpdatedAt(LocalDateTime.now());
                shippingStatusRepository.save(shippingStatus);

                Wallet userWallet = walletRepository.findByUserId(order.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ví của khách hàng"));
                Wallet sellerWallet = walletRepository.findByUserId(details.getProductId().getStoreId().getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ví của người bán"));

                // Hoàn tiền
                BigDecimal returnPrice = BigDecimal.valueOf(invoice.getDeposit().longValue());
                updateWalletBalance(userWallet, invoice.getDeposit().subtract(invoice.getShippingFee()),
                        "Đã hoàn " + (invoice.getDeposit().subtract(invoice.getShippingFee())) + " vì đơn hàng " + order.getOrderCode() + " giao không thành công",
                        String.valueOf(TransactionType.REFUND));
                updateWalletBalance(adminWallet.get(), invoice.getDeposit().subtract(invoice.getShippingFee()).negate(),
                        "Hoàn trả " + invoice.getDeposit().subtract(invoice.getShippingFee()) + " cho khách vì đơn hàng " + order.getOrderCode() + " không giao được",
                        String.valueOf(TransactionType.REFUND));
                updateWalletBalance(adminWallet.get(), invoice.getShippingFee().negate(),
                        "Trả " + invoice.getShippingFee().negate() + " tiền ship cho Logistic vì đơn hàng " + order.getOrderCode() + " không giao được",
                        String.valueOf(TransactionType.SHIPPING_FEE));
                updateWalletBalance(logisticWallet.get(), invoice.getShippingFee(),
                        "Nhận " + invoice.getShippingFee() + " cho đơn hàng " + order.getOrderCode(),
                        String.valueOf(TransactionType.SHIPPING_FEE));

                // Tạo thông báo
                createNotification(order.getUserId(), "Đơn hàng thất bại",
                        "Đơn hàng " + order.getOrderCode() + " giao thất bại. Số tiền đã hoàn: " + invoice.getDeposit().subtract(invoice.getShippingFee()),
                        "/order-detail/" + orderId
                );
//                createNotification(sellerWallet.getUserId(), "Đơn hàng thất bại",
//                        "Đơn hàng " + order.getOrderCode() + " giao thất bại. Đã hoàn tiền: " + invoice.getFormatedDeposit(),
//                        "/order-detail/" + orderId);
            }
        } else if (status.equalsIgnoreCase("Completed")) {
            if (sellWallet.isPresent() && adminWallet.isPresent()) {
//                System.out.println("Sell Wallet transaction completed: " + sellWallet);
//                System.out.println("Admin Wallet transaction completed: " + adminWallet);
                if (invoice.getRemainingBalance().compareTo(BigDecimal.ZERO) != 0) {
                    // Cập nhật ví admin
                    BigDecimal currentAdminWallet = new BigDecimal(adminWallet.get().getBalance());
                    BigDecimal subtractAmount = (invoice.getTotalAmount().add(invoice.getTax()).add(invoice.getShippingFee())).subtract(invoice.getRemainingBalance());
                    Wallet adminWallet2 = adminWallet.get();
                    System.out.println("Admin Wallet: " + adminWallet2.getBalance());
                    System.out.println("Subtract Amount: " + subtractAmount);
                    BigDecimal newAdminAmount = currentAdminWallet.subtract(subtractAmount);
                    adminWallet2.setBalance(newAdminAmount.longValue());
                    walletRepository.save(adminWallet2);

                    // Lưu giao dịch cho admin
                    Transactions adminTransaction = new Transactions();
                    adminTransaction.setCreatedAt(LocalDateTime.now());
                    adminTransaction.setToWalletId(adminWallet.get());
                    adminTransaction.setAmount(invoice.getTotalAmount().add(invoice.getTax()).negate().longValue());
                    adminTransaction.setTransactionType("Thanh toán đơn hàng");
                    adminTransaction.setDescription("Trả " + invoice.getTotalAmount().add(invoice.getTax()) + " cho người bán từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(adminTransaction);

// Cậ               cập nhật ví seller
                    BigDecimal currentSellerAmount = new BigDecimal(sellWallet.get().getBalance());
                    BigDecimal newSellerAmount = currentSellerAmount.add(invoice.getTotalAmount().add(invoice.getTax()));
                    sellWallet.get().setBalance(newSellerAmount.longValue());
                    walletRepository.save(sellWallet.get());

                    // Lưu giao dịch cho admin
                    Transactions sellerTransaction = new Transactions();
                    sellerTransaction.setCreatedAt(LocalDateTime.now());
                    sellerTransaction.setToWalletId(adminWallet.get());
                    sellerTransaction.setAmount(invoice.getRemainingBalance().abs().longValue());
                    sellerTransaction.setTransactionType("Nhận tiền hàng còn lại");
                    sellerTransaction.setDescription("Nhận " + invoice.getRemainingBalance() + " còn lại của khách từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(sellerTransaction);

                    Transactions logisticTransactions = new Transactions();
                    logisticTransactions.setCreatedAt(LocalDateTime.now());
                    logisticTransactions.setToWalletId(logisticWallet.get());
                    logisticTransactions.setAmount(invoice.getRemainingBalance().abs().longValue());
                    logisticTransactions.setTransactionType("Nhận tiền hàng còn lại");
                    logisticTransactions.setDescription("Nhận " + invoice.getRemainingBalance() + " còn lại từ khách hàng ");
                    transactionRepository.save(logisticTransactions);

                    Transactions logisticTransactions2 = new Transactions();
                    logisticTransactions2.setCreatedAt(LocalDateTime.now());
                    logisticTransactions2.setFromWalletId(logisticWallet.get());
                    logisticTransactions2.setAmount(invoice.getRemainingBalance().abs().longValue());
                    logisticTransactions2.setTransactionType("Trả tiền hàng");
                    logisticTransactions2.setDescription("Trả " + invoice.getRemainingBalance() + " cho trung gian của đơn hàng  " + order.getOrderCode());
                    transactionRepository.save(logisticTransactions2);

                    Transactions sellerTransaction2 = new Transactions();
                    sellerTransaction2.setCreatedAt(LocalDateTime.now());
                    sellerTransaction2.setToWalletId(sellWallet.get());
                    sellerTransaction2.setAmount(invoice.getTotalAmount().add(invoice.getTax()).abs().longValue());
                    sellerTransaction2.setTransactionType("Nhận tiền hàng");
                    sellerTransaction2.setDescription("Nhận " + invoice.getTotalAmount().add(invoice.getTax()) + " từ đơn hàng  " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(sellerTransaction2);
                    emailService.sendTransactionMailAsync(sellWallet.get().getUserId(), sellerTransaction2, invoice.getTotalAmount().add(invoice.getTax()).abs().longValue());

                    BigDecimal currentLogisticAmount = new BigDecimal(logisticWallet.get().getBalance());
                    BigDecimal newLogisticAmount = currentLogisticAmount.add(invoice.getShippingFee());
                    logisticWallet.get().setBalance(newLogisticAmount.longValue());
                    walletRepository.save(logisticWallet.get());

                    Transactions adminTransaction2 = new Transactions();
                    adminTransaction2.setCreatedAt(LocalDateTime.now());
                    adminTransaction2.setFromWalletId(adminWallet.get());
                    adminTransaction2.setAmount(invoice.getShippingFee().negate().longValue());
                    adminTransaction2.setTransactionType("Thanh toán tiền ship");
                    adminTransaction2.setDescription("Trả " + invoice.getFormattedShippingFee() + " cho Logistic từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(adminTransaction2);

                    Transactions logisticTransactions3 = new Transactions();
                    logisticTransactions3.setCreatedAt(LocalDateTime.now());
                    logisticTransactions3.setToWalletId(logisticWallet.get());
                    logisticTransactions3.setAmount(invoice.getShippingFee().abs().longValue());
                    logisticTransactions3.setTransactionType("Nhận tiền ship");
                    logisticTransactions3.setDescription("Nhận tiền ship " + invoice.getFormattedShippingFee() + " cho đơn hàng  " + order.getOrderCode());
                    transactionRepository.save(logisticTransactions3);

                    // Tạo thông báo
                    createNotification(sellWallet.get().getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " đã giao thành công. Số tiền nhận: " + invoice.getFormatedRemainingBalance(),
                            "/order-detail/" + orderId);
                    createNotification(order.getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " của bạn đã được giao thành công.",
                            "/order-detail/" + orderId);
                } else if (invoice.getRemainingBalance().compareTo(BigDecimal.ZERO) == 0) {
                    // Cập nhật ví admin
                    BigDecimal currentAdminWallet = new BigDecimal(adminWallet.get().getBalance());
                    BigDecimal subtractAmount = invoice.getTotalAmount().add(invoice.getTax()).add(invoice.getShippingFee());
                    Wallet adminWallet2 = adminWallet.get();
                    System.out.println("Admin Wallet: " + adminWallet2.getBalance());
                    System.out.println("Subtract Amount: " + subtractAmount);
                    BigDecimal newAdminAmount = currentAdminWallet.subtract(subtractAmount);
                    adminWallet2.setBalance(newAdminAmount.longValue());
                    walletRepository.save(adminWallet2);

                    // Lưu giao dịch cho admin
                    Transactions adminTransaction = new Transactions();
                    adminTransaction.setCreatedAt(LocalDateTime.now());
                    adminTransaction.setToWalletId(adminWallet.get());
                    adminTransaction.setAmount(invoice.getTotalAmount().add(invoice.getTax()).negate().longValue());
                    adminTransaction.setTransactionType("Thanh toán đơn hàng");
                    adminTransaction.setDescription("Trả " + invoice.getTotalAmount().add(invoice.getTax()) + " cho người bán từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(adminTransaction);

// Cậ               cập nhật ví seller
                    BigDecimal currentSellerAmount = new BigDecimal(sellWallet.get().getBalance());
                    BigDecimal newSellerAmount = currentSellerAmount.add(invoice.getTotalAmount().add(invoice.getTax()));
                    sellWallet.get().setBalance(newSellerAmount.longValue());
                    walletRepository.save(sellWallet.get());

                    Transactions sellerTransaction2 = new Transactions();
                    sellerTransaction2.setCreatedAt(LocalDateTime.now());
                    sellerTransaction2.setToWalletId(sellWallet.get());
                    sellerTransaction2.setAmount(invoice.getTotalAmount().add(invoice.getTax()).abs().longValue());
                    sellerTransaction2.setTransactionType("Nhận tiền hàng");
                    sellerTransaction2.setDescription("Nhận " + invoice.getTotalAmount().add(invoice.getTax()) + " từ đơn hàng  " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(sellerTransaction2);
                    emailService.sendTransactionMailAsync(sellWallet.get().getUserId(), sellerTransaction2, invoice.getTotalAmount().add(invoice.getTax()).abs().longValue());

                    BigDecimal currentLogisticAmount = new BigDecimal(logisticWallet.get().getBalance());
                    BigDecimal newLogisticAmount = currentLogisticAmount.add(invoice.getShippingFee());
                    logisticWallet.get().setBalance(newLogisticAmount.longValue());
                    walletRepository.save(logisticWallet.get());

                    Transactions adminTransaction2 = new Transactions();
                    adminTransaction2.setCreatedAt(LocalDateTime.now());
                    adminTransaction2.setFromWalletId(adminWallet.get());
                    adminTransaction2.setAmount(invoice.getShippingFee().negate().longValue());
                    adminTransaction2.setTransactionType("Thanh toán tiền ship");
                    adminTransaction2.setDescription("Trả " + invoice.getFormattedShippingFee() + " cho Logistic từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(adminTransaction2);

                    Transactions logisticTransactions3 = new Transactions();
                    logisticTransactions3.setCreatedAt(LocalDateTime.now());
                    logisticTransactions3.setToWalletId(logisticWallet.get());
                    logisticTransactions3.setAmount(invoice.getShippingFee().abs().longValue());
                    logisticTransactions3.setTransactionType("Nhận tiền ship");
                    logisticTransactions3.setDescription("Nhận tiền ship " + invoice.getFormattedShippingFee() + " cho đơn hàng  " + order.getOrderCode());
                    transactionRepository.save(logisticTransactions3);

                    // Tạo thông báo
                    createNotification(sellWallet.get().getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " đã giao thành công. Số tiền nhận: " + invoice.getFormatedRemainingBalance(),
                            "/order-detail/" + orderId);
                    createNotification(order.getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " của bạn đã được giao thành công.",
                            "/order-detail/" + orderId);
                }
            }
            shippingStatusRepository.updateOrderStatus(orderId, status);
            System.out.println("Order status updated to " + status);
        } else {
            shippingStatusRepository.updateOrderStatus(orderId, status);
        }
    }

    private void createNotification(Users user, String title, String content, String url) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setUrl(url); // URL tùy chỉnh
        notification.setUserId(user);
        notification.setCreated(LocalDateTime.now());
        notification.setRead(false); // Đánh dấu chưa đọc
        notificationService.updateNotification(notification);
    }

    private void updateWalletBalance(Wallet wallet, BigDecimal amount, String description, String transactionType) {
        BigDecimal currentBalance = BigDecimal.valueOf(wallet.getBalance());

        BigDecimal updatedBalance = currentBalance.add(amount);

        wallet.setBalance(updatedBalance.longValue());
        walletRepository.save(wallet);

        Transactions transaction = new Transactions();
        transaction.setFromWalletId(amount.signum() < 0 ? wallet : null);
        transaction.setToWalletId(amount.signum() > 0 ? wallet : null);
        transaction.setAmount(amount.abs().longValue());
        transaction.setTransactionType(transactionType);
        transaction.setDescription(description);
        transaction.setIsRefund("YES");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        emailService.sendTransactionMailAsync(wallet.getUserId(), transaction, amount.longValue());
    }

    @Transactional
    public Orders createOrder(Users user, String address, PaymentMethod paymentMethod, List<Integer> cartItemIds) {
        Orders order = new Orders();
        order.setUserId(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(address);
        order.setPaymentMethod(paymentMethod);
        order.setOrderCode(RandomOrderCodeGenerator.generateOrderCode());

        long totalOrderPrice = 0;
        List<OrderDetails> orderDetailsList = new ArrayList<>();

        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemService.getCartItemById(cartItemId);
            if (cartItem != null) {
                OrderDetails orderDetail = new OrderDetails();
                orderDetail.setOrderId(order);
                orderDetail.setProductId(cartItem.getProductId());
                orderDetail.setVariationId(cartItem.getVariationId());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(cartItem.getPrice());
                totalOrderPrice += (long) cartItem.getPrice() * cartItem.getQuantity();
                orderDetailsList.add(orderDetail);

                // Update inventory

//                ProductVariation variation = productVariationRepository.findById(cartItem.getVariationId().getVariationId()).orElse(null);
//                if (variation != null) {
//                    variation.setStock(variation.getStock() - cartItem.getQuantity());
//                    System.out.println("Stock Before: " + variation.getStock());
//                    System.out.println("Cart Item Quantity: " + cartItem.getQuantity());
//                    System.out.println("Stock After: " + (variation.getStock() - cartItem.getQuantity()));
//                    productVariationRepository.save(variation);
//                }
            }
        }
        System.out.println(user.getUserId());
        cartService.subtractItemQuantitiesFromStock(user.getUserId());

        ShippingStatus initialShippingStatus = ShippingStatus.builder()
                .orderId(order)
                .status("Pending")
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the order to generate the order ID
        order.setTotalPrice(totalOrderPrice / 2);
        orderRepository.save(order);

        // Save order details
        orderDetailRepository.saveAll(orderDetailsList);

        // Save initial shipping status
        shippingStatusRepository.save(initialShippingStatus);

        // Check for duplicate order details and remove them
        List<OrderDetails> savedOrderDetails = orderDetailRepository.findByOrderId(order.getOrderId());
        Set<String> uniqueOrderDetails = new HashSet<>();
        for (OrderDetails orderDetail : savedOrderDetails) {
            String uniqueKey = orderDetail.getProductId().getProductId() + "-" + orderDetail.getVariationId().getVariationId();
            if (!uniqueOrderDetails.add(uniqueKey)) {
                orderDetailRepository.delete(orderDetail);
            }
        }

        return order;
    }

    @Override
    public Orders getOrderByCode(String code) {
        return orderRepository.findFirstByOrderCode(code);
    }

    @Override
    public List<OrderManagerDTO> getOrders() {
        List<Object[]> results = orderRepository.findOrders();
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId(result[0] != null ? ((Number) result[0]).intValue() : null);
            order.setCustomerName(result[1] != null ? result[1].toString() : "");
            order.setProductNames(result[2] != null ? result[2].toString() : "");
            order.setTotalProducts(result[3] != null ? ((Number) result[3]).intValue() : 0);
            order.setTotalPrice(result[4] != null ? ((Number) result[4]).intValue() : 0);

            // Map orderDate from result[5]
            if (result[5] != null) { // orderDate
                if (result[5] instanceof java.sql.Date) {
                    order.setOrderDate(((java.sql.Date) result[5]).toLocalDate());
                } else if (result[5] instanceof java.sql.Timestamp) {
                    order.setOrderDate(((java.sql.Timestamp) result[5]).toLocalDateTime().toLocalDate());
                } else if (result[5] instanceof LocalDate) {
                    order.setOrderDate((LocalDate) result[5]);
                } else {
                    order.setOrderDate(null);
                }
            } else {
                order.setOrderDate(null);
            }

            order.setLatestStatus(result[6] != null ? result[6].toString() : "");
            order.setReason(result[7] != null ? result[7].toString() : "");
            order.setPrevious_status(result[8] != null ? result[8].toString() : "");

            orders.add(order);
        }
        return orders;
    }


    @Override
    public List<OrderManagerDTO> getOrdersByStatus(String status) {
        List<Object[]> results = orderRepository.findOrdersByStatus(status);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId((Integer) result[0]);
            order.setCustomerName((String) result[1]);
            order.setProductNames((String) result[2]);
            order.setTotalProducts(((Number) result[3]).intValue());
            order.setTotalPrice(((Number) result[4]).intValue());
            order.setLatestStatus((String) result[5]);
            orders.add(order);
        }
        return orders;
    }

    @Override
    public List<OrderManagerDTO> getOrdersByFilters(String status, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepository.findOrdersByFilters(status, startDate, endDate);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId((Integer) result[0]);
            order.setCustomerName((String) result[1]);
            order.setProductNames((String) result[2]);
            order.setTotalProducts(((Number) result[3]).intValue());
            order.setTotalPrice(((Number) result[4]).intValue());
            order.setLatestStatus((String) result[5]);
            if (result[6] != null) {
                if (result[6] instanceof java.sql.Date) {
                    order.setOrderDate(((java.sql.Date) result[6]).toLocalDate());
                } else if (result[6] instanceof java.sql.Timestamp) {
                    order.setOrderDate(((java.sql.Timestamp) result[6]).toLocalDateTime().toLocalDate());
                } else {
                    order.setOrderDate(null);
                }
            } else {
                order.setOrderDate(null);
            }
            orders.add(order);
        }
        return orders;
    }

    @Override
    public List<OrderManagerDTO> getOrdersByFilters(String status, LocalDate startDate, LocalDate endDate, Integer storeId) {
        List<Object[]> results = orderRepository.findOrdersByFiltersForStore(status, startDate, endDate, storeId);
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId((Integer) result[0]);
            order.setCustomerName((String) result[1]);
            order.setProductNames((String) result[2]);
            order.setTotalProducts(((Number) result[3]).intValue());
            order.setTotalPrice(((Number) result[4]).intValue());
            order.setLatestStatus((String) result[5]);
            if (result[6] != null) {
                if (result[6] instanceof java.sql.Date) {
                    order.setOrderDate(((java.sql.Date) result[6]).toLocalDate());
                } else if (result[6] instanceof java.sql.Timestamp) {
                    order.setOrderDate(((java.sql.Timestamp) result[6]).toLocalDateTime().toLocalDate());
                } else {
                    order.setOrderDate(null);
                }
            } else {
                order.setOrderDate(null);
            }
            orders.add(order);
        }
        return orders;
    }

    @Override
    @Transactional
    public void bulkUpdateOrderStatus(String currentStatus, String newStatus, LocalDate startDate, LocalDate endDate, Integer storeId) {
        List<OrderManagerDTO> ordersToUpdate = getOrdersByFilters(currentStatus, startDate, endDate, storeId);

        for (OrderManagerDTO orderDTO : ordersToUpdate) {
            updateOrderStatuss(orderDTO.getOrderId(), newStatus);
        }
    }

    @Override
    public void bulkUpdateOrderStatus(String currentStatus, String newStatus, LocalDate startDate, LocalDate endDate) {
        List<OrderManagerDTO> ordersToUpdate = getOrdersByFilters(currentStatus, startDate, endDate);
        for (OrderManagerDTO orderDTO : ordersToUpdate) {
            updateOrderStatuss(orderDTO.getOrderId(), newStatus);
        }
    }
}
