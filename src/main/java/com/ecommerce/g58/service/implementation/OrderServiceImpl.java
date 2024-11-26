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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;
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
    public Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable) {
        Page<Object[]> results = orderRepository.findOrdersByUserIdAndStatus(userId, status, pageable);
        return results.map(result -> {
            OrdersDTO dto = new OrdersDTO();
            dto.setOrderId((int) result[0]);

            if (result[1] != null) {
                Timestamp orderDateTimestamp = (Timestamp) result[1];
                dto.setOrderDate(orderDateTimestamp.toLocalDateTime());
            }

            dto.setStatus((String) result[2]);

            if (result[3] != null) {
                if (result[3] instanceof Number) {
                    dto.setTotalQuantity(((Number) result[3]).intValue());
                }
            }

            if (result[4] != null) {
                if (result[4] instanceof Number) {
                    dto.setTotalPrice(((Number) result[4]).longValue());
                }
            }
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
            order.setOrderId((Integer) result[0]);
            order.setCustomerName((String) result[1]);
            order.setProductNames((String) result[2]);
            order.setTotalProducts(((Number) result[3]).intValue());
            order.setTotalPrice(((Number) result[4]).intValue());
            order.setLatestStatus((String) result[5]);
            order.setReason((String) result[6]);
            order.setPrevious_status((String) result[7]);
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
        Optional<Wallet> logisticWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5);

        Invoice invoice = invoiceRepository.findInvoiceByOrderId(order);

        // Xử lý theo trạng thái đơn hàng
        if (status.equalsIgnoreCase("Dispatched")) {
            if (sellWallet.isPresent() && logisticWallet.isPresent()) {
                // Trừ phí vận chuyển từ ví người bán
                BigDecimal currentSellerWallet = new BigDecimal(sellWallet.get().getBalance());
                BigDecimal newSellerAmount = currentSellerWallet.subtract(invoice.getShippingFee());
                sellWallet.get().setBalance(newSellerAmount.longValue());
                walletRepository.save(sellWallet.get());

                // Thêm phí vận chuyển vào ví logistics
                BigDecimal currentLogisticWallet = new BigDecimal(logisticWallet.get().getBalance());
                BigDecimal newLogisticAmount = currentLogisticWallet.add(invoice.getShippingFee());
                logisticWallet.get().setBalance(newLogisticAmount.longValue());
                walletRepository.save(logisticWallet.get());

                // Lưu giao dịch cho người bán
                Transactions sellerTransactions = new Transactions();
                sellerTransactions.setCreatedAt(LocalDateTime.now());
                sellerTransactions.setFromWalletId(sellWallet.get());
                sellerTransactions.setAmount(invoice.getShippingFee().negate().longValue());
                sellerTransactions.setTransactionType("Thanh toán tiền ship");
                sellerTransactions.setDescription("Thanh toán " + invoice.getShippingFee() + " tiền ship cho đơn hàng " + order.getOrderCode());
                transactionRepository.save(sellerTransactions);

                // Lưu giao dịch cho logistics
                Transactions logisticTransactions = new Transactions();
                logisticTransactions.setCreatedAt(LocalDateTime.now());
                logisticTransactions.setToWalletId(logisticWallet.get());
                logisticTransactions.setAmount(invoice.getShippingFee().longValue());
                logisticTransactions.setTransactionType("Tiền ship");
                logisticTransactions.setDescription("Nhận " + invoice.getShippingFee() + " từ đơn hàng " + order.getOrderCode());
                transactionRepository.save(logisticTransactions);

                // Tạo thông báo
                createNotification(sellWallet.get().getUserId(), "Đơn hàng đã được gửi đi",
                        "Đơn hàng " + order.getOrderCode() + " đã được gửi đi. Phí vận chuyển: " + invoice.getShippingFee(),
                        "http://localhost:8080/order-detail/" + orderId);

            }
        } else if (status.equalsIgnoreCase("Delivered")) {
            if (sellWallet.isPresent() && logisticWallet.isPresent()) {
                if (invoice.getRemainingBalance().compareTo(BigDecimal.ZERO) != 0) {
                    // Cập nhật ví người bán
                    BigDecimal currentSellerWallet = new BigDecimal(sellWallet.get().getBalance());
                    BigDecimal newSellerAmount = currentSellerWallet.add(invoice.getRemainingBalance());
                    sellWallet.get().setBalance(newSellerAmount.longValue());
                    walletRepository.save(sellWallet.get());

                    // Lưu giao dịch
                    Transactions sellerTransactions = new Transactions();
                    sellerTransactions.setCreatedAt(LocalDateTime.now());
                    sellerTransactions.setToWalletId(sellWallet.get());
                    sellerTransactions.setAmount(invoice.getRemainingBalance().abs().longValue());
                    sellerTransactions.setTransactionType("Thanh toán đơn hàng");
                    sellerTransactions.setDescription("Nhận " + invoice.getRemainingBalance() + " từ đơn hàng " + order.getOrderCode() + " giao thành công");
                    transactionRepository.save(sellerTransactions);

                    // Tạo thông báo
                    createNotification(sellWallet.get().getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " đã giao thành công. Số tiền nhận: " + invoice.getRemainingBalance(),
                            "http://localhost:8080/order-detail/" + orderId);
                    createNotification(order.getUserId(), "Đơn hàng giao thành công",
                            "Đơn hàng " + order.getOrderCode() + " của bạn đã được giao thành công.",
                            "http://localhost:8080/order-detail/" + orderId);
                }
            }
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
                updateWalletBalance(userWallet, returnPrice,
                        "Đã hoàn " + invoice.getDeposit() + " vì đơn hàng " + order.getOrderCode() + " giao không thành công",
                        String.valueOf(TransactionType.REFUND));
                updateWalletBalance(sellerWallet, invoice.getDeposit().negate(),
                        "Hoàn trả " + invoice.getDeposit() + " cho khách vì đơn hàng " + order.getOrderCode() + " không giao được",
                        String.valueOf(TransactionType.REFUND));

                // Tạo thông báo
                createNotification(order.getUserId(), "Đơn hàng thất bại",
                        "Đơn hàng " + order.getOrderCode() + " giao thất bại. Số tiền đã hoàn: " + invoice.getDeposit(),
                        "http://localhost:8080/order-detail/" + orderId
                );
                createNotification(sellerWallet.getUserId(), "Đơn hàng thất bại",
                        "Đơn hàng " + order.getOrderCode() + " giao thất bại. Đã hoàn tiền: " + invoice.getDeposit(),
                        "http://localhost:8080/order-detail/" + orderId);
            }
        } else {
            shippingStatusRepository.updateOrderStatus(orderId, status);
        }
        shippingStatusRepository.updateOrderStatus(orderId, status);
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
                ProductVariation variation = productVariationRepository.findById(cartItem.getVariationId().getVariationId()).orElse(null);
                if (variation != null) {
                    variation.setStock(variation.getStock() - cartItem.getQuantity());
                    productVariationRepository.save(variation);
                }
            }
        }

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
    public List<OrderManagerDTO> getOrders() {
        List<Object[]> results = orderRepository.findOrders();
        List<OrderManagerDTO> orders = new ArrayList<>();
        for (Object[] result : results) {
            OrderManagerDTO order = new OrderManagerDTO();
            order.setOrderId((Integer) result[0]);
            order.setCustomerName((String) result[1]);
            order.setProductNames((String) result[2]);
            order.setTotalProducts(((Number) result[3]).intValue());
            order.setTotalPrice(((Number) result[4]).intValue());
            order.setLatestStatus((String) result[5]);
            order.setReason((String) result[6]);
            order.setPrevious_status((String) result[7]);
            orders.add(order);
        }
        return orders;
    }
}
