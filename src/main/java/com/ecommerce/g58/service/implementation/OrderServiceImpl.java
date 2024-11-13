package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.OrderDetailRepository;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.repository.ShippingStatusRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

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
        order.setTotalPrice(totalOrderPrice);
        order.setOrderDetails(orderDetailsList);
        orderRepository.save(order);
        return order;
    }
}
