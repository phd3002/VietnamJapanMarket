package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.controller.CheckoutController;
import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.dto.OrderDetailManagerDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.Reason;
import com.ecommerce.g58.enums.TransactionType;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.NotificationService;
import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.OrderDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private final OrderDetailRepository orderDetailRepository;
    private final FeedbackRepository feedbackRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    WalletRepository walletRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ShippingStatusRepository shippingStatusRepository;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository, FeedbackRepository feedbackRepository, ProductVariationRepository productVariationRepository, UserRepository userRepository,
                                  OrderRepository orderRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.feedbackRepository = feedbackRepository;
        this.productVariationRepository = productVariationRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetails = new ArrayList<>();

        for (Object[] result : results) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderId(orderId);
            dto.setProductId(((Integer) result[1]).longValue());
            dto.setStoreId(((Integer) result[2]).longValue());
            dto.setProductName((String) result[3]);
            dto.setProductImage((String) result[4]);
            dto.setCategoryName((String) result[5]);
            dto.setSizeAndColor((String) result[6]);
//            dto.setOrderTotalPrice(((Integer) result[7]));
            if (result[7] instanceof BigInteger) {
                dto.setOrderTotalPrice(((BigInteger) result[7]).intValue());
            } else if (result[7] instanceof Integer) {
                dto.setOrderTotalPrice((Integer) result[7]);
            }
            dto.setQuantity((Integer) result[8]);
            dto.setProductTotalPrice((Integer) result[9]);
            dto.setAvgRating((Integer) result[10]);
            dto.setStoreName((String) result[11]);
            dto.setStoreImage((String) result[12]);  // Added
            dto.setTotalAmount((Integer) result[13]);
            dto.setShippingFee((Integer) result[14]);
            dto.setPaymentMethod((String) result[15]);
            dto.setPaymentStatus((String) result[16]);
            dto.setShippingAddress((String) result[17]);
            dto.setShippingStatus((String) result[18]);
            dto.setPreviousStatus((String) result[19]);
            dto.setOrderCode((String) result[20]);
            if (result[21] instanceof Timestamp) {
                Timestamp pendingTimestamp = (Timestamp) result[21];
                dto.setPendingTime(pendingTimestamp.toLocalDateTime());
            }
            if (result[22] instanceof Timestamp) {
                Timestamp confirmedTimestamp = (Timestamp) result[22];
                dto.setConfirmedTime(confirmedTimestamp.toLocalDateTime());
            }
            if (result[23] instanceof Timestamp) {
                Timestamp processingTimestamp = (Timestamp) result[23];
                dto.setProcessingTime(processingTimestamp.toLocalDateTime());
            }
            if (result[24] instanceof Timestamp) {
                Timestamp dispatchedTimestamp = (Timestamp) result[24];
                dto.setDispatchedTime(dispatchedTimestamp.toLocalDateTime());
            }
            if (result[24] instanceof Timestamp) {
                Timestamp shippingTimestamp = (Timestamp) result[24];
                dto.setShippingTime(shippingTimestamp.toLocalDateTime());
            }
            if (result[25] instanceof Timestamp) {
                Timestamp failedTimestamp = (Timestamp) result[25];
                dto.setFailedTime(failedTimestamp.toLocalDateTime());
            }
            if(result[26] instanceof Timestamp) {
                Timestamp failedTimestamp = (Timestamp) result[26];
                dto.setFailedTime(failedTimestamp.toLocalDateTime());
            }
            if (result[27] instanceof Timestamp) {
                Timestamp deliveredTimestamp = (Timestamp) result[27];
                dto.setDeliveredTime(deliveredTimestamp.toLocalDateTime());
            }
            if (result[28] instanceof Timestamp) {
                Timestamp completedTimestamp = (Timestamp) result[28];
                dto.setCompletedTime(completedTimestamp.toLocalDateTime());
            }
            if (result[29] instanceof Timestamp) {
                Timestamp cancelledTimestamp = (Timestamp) result[29];
                dto.setCancelledTime(cancelledTimestamp.toLocalDateTime());
            }
            if (result[30] instanceof Timestamp) {
                Timestamp returnedTimestamp = (Timestamp) result[30];
                dto.setReturnedTime(returnedTimestamp.toLocalDateTime());
            }
            orderDetails.add(dto);
        }
        return orderDetails;
    }

    @Transactional
    @Override
    public void rateOrder(Long orderId, String userEmail, String rateText, Integer rateStar) {
        List<OrderDetailDTO> orderDetails = getOrderDetails(orderId);
        if (orderDetails.isEmpty()) throw new RuntimeException();

        var user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException();

        var orderDetail = orderDetails.get(0);
        var productId = orderDetail.getProductId();
        var productVariants = productVariationRepository.findByProductIdProductId(productId.intValue());

        for (var pv : productVariants) {
            var storeId = pv.getProductId().getStoreId();
            var feedback = new Feedback();
            feedback.setStoreId(storeId);
            feedback.setUserId(user);
            feedback.setRating(rateStar);
            feedback.setComment(rateText);
            feedback.setVariationId(pv);
            feedback.setCreatedAt(LocalDateTime.now());
            feedbackRepository.save(feedback);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Integer orderId, String status, String reason) {
        var orderStatus = shippingStatusRepository.findByOrderIdOrderId(orderId);
        if (orderStatus.getStatus().equals("Pending") || orderStatus.getStatus().equals("Processing")
                || (orderStatus.getStatus().equals("Delivered") && reason != null)) {
            orderStatus.setPrevious_status(orderStatus.getStatus());
        }
        orderStatus.setStatus(status);
        if (status.equals("Returned") && reason != null) {
            orderStatus.setReason(reason);
        }
    }

    @Override
    public List<OrderDetailManagerDTO> getOrderDetailsForManager(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetailsByOrderId(orderId);
        List<OrderDetailManagerDTO> orderDetailManager = new ArrayList<>();
        for (Object[] result : results) {
            OrderDetailManagerDTO dto = new OrderDetailManagerDTO();
            dto.setStoreName((String) result[0]);
            dto.setStoreAddress((String) result[1]);
            dto.setStorePhone((String) result[2]);
            dto.setOrderStatus((String) result[3]);
            dto.setOrderCode((String) result[4]);
//            dto.setStatusTime((LocalDateTime) result[5]);
            if (result[5] instanceof Timestamp) {
                Timestamp statusTimestamp = (Timestamp) result[5];
                dto.setStatusTime(statusTimestamp.toLocalDateTime());
            }
            dto.setCustomerName((String) result[6]);
            dto.setCustomerAddress((String) result[7]);
            dto.setCustomerPhone((String) result[8]);
//            dto.setOrderDate((LocalDateTime) result[9]);
            if (result[9] instanceof Timestamp) {
                Timestamp orderTimestamp = (Timestamp) result[9];
                dto.setOrderDate(orderTimestamp.toLocalDateTime());
            }
            dto.setProductName((String) result[10]);
            dto.setProductType((String) result[11]);
            dto.setQuantity((Integer) result[12]);
            dto.setProductPrice((Integer) result[13]);
//            dto.setTotalPrice((Integer) result[14]);
            if (result[14] instanceof BigInteger) {
                dto.setTotalPrice(((BigInteger) result[14]).intValue());
            } else if (result[14] instanceof Integer) {
                dto.setTotalPrice((Integer) result[14]);
            }
            dto.setShippingFee((Integer) result[15]);
            dto.setTax((Integer) result[16]);
            dto.setPaymentMethod((String) result[17]);
//            dto.setTotalAmount((Integer) result[18]);
            if (result[18] instanceof BigDecimal) {
                dto.setTotalAmount(((BigDecimal) result[18]).intValue());
            } else if (result[18] instanceof Integer) {
                dto.setTotalAmount((Integer) result[18]);
            }
            orderDetailManager.add(dto);
        }
        return orderDetailManager;
    }


    @Override
    public boolean changeStatus(Integer orderId, String status, String reason) {
        ShippingStatus shippingStatus = shippingStatusRepository.findShippingStatusByOrderId_OrderId(orderId);
        if (shippingStatus == null) {
            logger.error("Order not found");
            return false;
        }
        shippingStatus.setStatus(status);
        shippingStatus.setUpdatedAt(LocalDateTime.now());
        String actualReason;
        if (reason.equalsIgnoreCase(String.valueOf(Reason.NOT_AS_DESCRIBED))) {
            actualReason = "Hàng không giống mô tả";
        } else if (reason.equalsIgnoreCase(String.valueOf(Reason.DAMAGED))) {
            actualReason = "Hàng bị vỡ/hỏng";
        } else {
            actualReason = reason;
        }
        shippingStatus.setReason(actualReason);
        shippingStatusRepository.save(shippingStatus);


        return true;
    }

    @Override
    public boolean cancelOrder(Integer orderId, String status, String reason) {
        boolean isChanged = changeStatus(orderId, status, reason);
        if (isChanged) {
            Orders order = orderRepository.findOrdersByOrderId(orderId);
            Invoice invoice = invoiceRepository.findInvoiceByOrderId(order);
            if (invoice == null) {
                logger.error("Invoice not found");
                return false;
            }
            OrderDetails details = orderDetailRepository.findByOrderId((orderId)).get(0);
            Optional<Wallet> sellerWallet = walletRepository.findByUserId(details.getProductId().getStoreId().getOwnerId());
            Optional<Wallet> userWallet = walletRepository.findByUserId(order.getUserId());
            if (sellerWallet.isPresent() && userWallet.isPresent()) {
                BigDecimal currentSellerBalance = new BigDecimal(sellerWallet.get().getBalance());
                BigDecimal newSellerBalance = currentSellerBalance.subtract(invoice.getDeposit());
                sellerWallet.get().setBalance(newSellerBalance.longValue());

                walletRepository.save(sellerWallet.get());

                BigDecimal walletBalance = new BigDecimal(userWallet.get().getBalance());
                BigDecimal totalAmount = walletBalance.add(invoice.getDeposit());
                userWallet.get().setBalance(totalAmount.longValue());

                walletRepository.save(userWallet.get());

                // seller transaction
                Transactions sellerTransactions = new Transactions();
                sellerTransactions.setAmount(-invoice.getDeposit().longValue());
                sellerTransactions.setFromWalletId(sellerWallet.get());
                sellerTransactions.setTransactionType("Hoàn tiền");
                sellerTransactions.setDescription("Hoàn " + invoice.getDeposit() + " do khách hủy đơn " + order.getOrderCode());
                sellerTransactions.setCreatedAt(LocalDateTime.now());

                transactionRepository.save(sellerTransactions);

                Transactions userTransactions = new Transactions();
                userTransactions.setAmount(invoice.getDeposit().longValue());
                userTransactions.setToWalletId(userWallet.get());
                userTransactions.setTransactionType("Thanh toán hoàn tiền");
                userTransactions.setDescription("Hoàn " + invoice.getDeposit() + "tiền từ đơn hàng  do bạn đã hủy đơn " + order.getOrderCode());
                userTransactions.setCreatedAt(LocalDateTime.now());


                transactionRepository.save(userTransactions);
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean refundOrder(Integer orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }

        System.out.println(authentication.getName());

        // Fetch the order and validate ownership
        Orders order = orderRepository.findOrdersByOrderId(orderId);


        // Fetch the invoice for shipping fee and additional calculations
        Invoice invoice = invoiceRepository.findInvoiceByOrderId(order);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found for the order");
        }

        OrderDetails orderDetails = orderDetailRepository.findByOrderId(orderId).get(0);
        ShippingStatus shippingStatus = shippingStatusRepository.findShippingStatusByOrderId_OrderId(orderId);

        // Handle refund logic based on reason
        if (shippingStatus.getReason().equalsIgnoreCase("Hàng không giống mô tả")) {
            handleNotAsDescribedRefund(order, orderDetails, invoice);
        } else if (shippingStatus.getReason().equalsIgnoreCase("Hàng bị vỡ/hỏng")) {
            handleDamagedRefund(order, orderDetails, invoice);
        } else {
            throw new IllegalArgumentException("Invalid refund reason");
        }
        // Update shipping status
        shippingStatus.setStatus("Returned");
        shippingStatus.setPrevious_status("Returned");
        shippingStatusRepository.save(shippingStatus);
        return true;
    }

    private void handleNotAsDescribedRefund(Orders order, OrderDetails detail, Invoice invoice) {
        Wallet userWallet = walletRepository.findByUserId(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User wallet not found"));

        // Refund logic
        BigDecimal returnPrice = BigDecimal.valueOf(invoice.getDeposit().longValue());
        // Refund full product price to the user and deduct from seller's wallet
        Wallet sellerWallet = walletRepository.findByUserId(detail.getProductId().getStoreId().getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller wallet not found"));

        // Hoàn tiền đơn hàng + phí ship(lượt đi) cho user
        updateWalletBalance(
                userWallet,
                returnPrice,
                "Nhận " + invoice.getDeposit() + " do yêu cầu hoàn trả đơn hàng " + order.getOrderCode() + " đã được chấp nhận",
                String.valueOf(TransactionType.REFUND)
        );
        Notification notification = new Notification();
        notification.setTitle("Hoàn tiền: " + invoice.getDeposit());
        notification.setContent("Nhận " + invoice.getDeposit() + " do yêu cầu hoàn trả đơn hàng " + order.getOrderCode() + " đã được chấp nhận");

        notification.setUrl("http://localhost:8080/wallet");
        notification.setUserId(order.getUserId());

        notificationService.updateNotification(notification);
        // Trừ tiền ví seller ch0 đơn hàng + phí ship(lượt đi)
        updateWalletBalance(
                sellerWallet,
                invoice.getDeposit().negate(),
                "Hoàn trả  " + invoice.getDeposit() + " cho khách do đơn hàng " + order.getOrderCode() + " không giống mô tả!",
                String.valueOf(TransactionType.REFUND));

        Notification sellerNotification1 = new Notification();
        sellerNotification1.setTitle("Hoàn tiền: " + invoice.getDeposit());
        sellerNotification1.setContent("Hoàn trả  " + invoice.getDeposit() + " cho khách do đơn hàng " + order.getOrderCode() + " không giống mô tả!");
        sellerNotification1.setUrl("http://localhost:8080/wallet");
        sellerNotification1.setUserId(sellerWallet.getUserId());

        notificationService.updateNotification(sellerNotification1);
        // Deduct shipping fee from seller's wallet and add to logistic
        Wallet logisticWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5)
                .orElseThrow(() -> new IllegalArgumentException("Logistic wallet not found"));

        // trừ tiền ship lượt về cho shipper
        updateWalletBalance(
                sellerWallet,
                invoice.getShippingFee().negate(),
                "Trả tiền ship lượt về cho đơn hàng " + order.getOrderCode(),
                String.valueOf(TransactionType.SHIPPING_FEE)
        );
        // nhận tiền ship lượt về
        updateWalletBalance(
                logisticWallet,
                invoice.getShippingFee(),
                "Nhận tiền ship lượt về cho đơn hàng " + order.getOrderCode(),
                String.valueOf(TransactionType.SHIPPING_FEE)

        );
    }

    private void handleDamagedRefund(Orders order, OrderDetails detail, Invoice invoice) {
        Wallet userWallet = walletRepository.findByUserId(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User wallet not found"));

        // Refund logic
        Wallet sellerWallet = walletRepository.findByUserId(detail.getProductId().getStoreId().getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller wallet not found"));

        Wallet logisticWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5)
                .orElseThrow(() -> new IllegalArgumentException("Logistic wallet not found"));

        // Refund full product price to the user and deduct from seller's wallet

        updateWalletBalance(
                sellerWallet,
                invoice.getDeposit().negate(),
                "Hoàn trả  " + invoice.getDeposit() + " cho khách do đơn hàng " + order.getOrderCode() + " không giống mô tả!",
                String.valueOf(TransactionType.REFUND));
        Notification notification = new Notification();
        notification.setTitle("Hoàn tiền: " + invoice.getDeposit());
        notification.setContent("Hoàn trả  " + invoice.getDeposit() + " cho khách do đơn hàng " + order.getOrderCode() + " không giống mô tả!");
        notification.setUrl("http://localhost:8080/wallet");
        notification.setUserId(sellerWallet.getUserId());

        notificationService.updateNotification(notification);

        updateWalletBalance(
                userWallet,
                invoice.getDeposit(),
                "Nhận " + invoice.getDeposit() + " do yêu cầu hoàn trả đơn hàng " + order.getOrderCode() + " đã được chấp nhận",
                String.valueOf(TransactionType.REFUND)

                // Logistic

        );
        Notification userNotification = new Notification();
        userNotification.setTitle("Hoàn tiền: " + invoice.getDeposit());
        userNotification.setContent("Nhận " + invoice.getDeposit() + " do yêu cầu hoàn trả đơn hàng " + order.getOrderCode() + " đã được chấp nhận");
        userNotification.setUrl("http://localhost:8080/wallet");
        userNotification.setUserId(order.getUserId());

        notificationService.updateNotification(userNotification);
        // Logistic đền cho seller tiền hàng (không bao gồm ship) - coi như seller mất tiền ship 1 lần
        BigDecimal returnPrice = BigDecimal.valueOf((invoice.getDeposit().longValue())).subtract(BigDecimal.valueOf(invoice.getShippingFee().longValue()));
        updateWalletBalance(
                logisticWallet,
                returnPrice.negate(),
                "Bồi thường do làm hỏng  đơn hàng " + order.getOrderCode(),
                String.valueOf(TransactionType.REFUND)

        );
        updateWalletBalance(
                sellerWallet,
                returnPrice,
                "Nhận tiền bồi thường cho đơn hàng " + order.getOrderCode() + " do ship làm hỏng",
                String.valueOf(TransactionType.REFUND)

        );
        Notification sellerNotification1 = new Notification();
        sellerNotification1.setTitle("Hoàn tiền: " + invoice.getDeposit());
        sellerNotification1.setContent("Nhận tiền bồi thường cho đơn hàng " + order.getOrderCode() + " do ship làm hỏng");
        sellerNotification1.setUrl("http://localhost:8080/wallet");
        sellerNotification1.setUserId(order.getUserId());

        notificationService.updateNotification(sellerNotification1);

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
    }
}