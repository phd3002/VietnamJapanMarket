package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.UserService;
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
        if (status.equalsIgnoreCase("Dispatched")) {
            // get order
            Orders order = orderRepository.findOrdersByOrderId(orderId);
            // get detail to get store Owner
            OrderDetails details = orderDetailRepository.findByOrderId(orderId).get(0);

            Optional<Wallet> sellWallet = walletRepository.findByUserId(details.getProductId().getStoreId().getOwnerId());
            Optional<Wallet> logisticWallet = walletRepository.findFirstByUserId_RoleId_RoleId(5);

            Invoice invoice = invoiceRepository.findInvoiceByOrderId(order);


            if (sellWallet.isPresent() && logisticWallet.isPresent()) {
                // process to subtract shipping fee from seller balance
                BigDecimal currentSellerWallet = new BigDecimal(sellWallet.get().getBalance());
                BigDecimal newSellerAmount = currentSellerWallet.subtract(invoice.getShippingFee());
                sellWallet.get().setBalance(newSellerAmount.longValue());
                // save to database
                walletRepository.save(sellWallet.get());

                //process to plus shipping fee to logistic balance
                BigDecimal currentLogisticWallet = new BigDecimal(sellWallet.get().getBalance());
                BigDecimal newLogisticAmount = currentLogisticWallet.add(invoice.getShippingFee());
                sellWallet.get().setBalance(newLogisticAmount.longValue());
                // save to database
                walletRepository.save(logisticWallet.get());

                // process to save transaction for seller
                Transactions sellerTransactions = new Transactions();
                sellerTransactions.setCreatedAt(LocalDateTime.now());
                sellerTransactions.setFromWalletId(sellWallet.get());
                sellerTransactions.setAmount(invoice.getShippingFee().longValue());
                sellerTransactions.setTransactionType("Thanh toán tiền ship");
                sellerTransactions.setDescription("Thanh toán " + invoice.getShippingFee() + " tiền ship cho đơn hàng " + order.getOrderCode());

                transactionRepository.save(sellerTransactions);
                // process to save transaction for seller
                Transactions logisticTransactions = new Transactions();
                logisticTransactions.setCreatedAt(LocalDateTime.now());
                logisticTransactions.setToWalletId(logisticWallet.get());
                logisticTransactions.setAmount(invoice.getShippingFee().longValue());
                logisticTransactions.setTransactionType("Tiền ship");
                logisticTransactions.setDescription("Nhận " + invoice.getShippingFee() + " từ đơn hàng " + order.getOrderCode());

                transactionRepository.save(logisticTransactions);
            }
        }
        shippingStatusRepository.updateOrderStatus(orderId, status);
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
            orders.add(order);
        }
        return orders;
    }
}
