package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Page<OrdersDTO> getOrdersByUserIdAndStatusAndDate(Integer userId, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<OrderManagerDTO> getOrdersForStore(Integer userId);

    List<OrderManagerDTO> getOrdersByStoreId(Integer storeId);

    void updateOrderStatus(Orders orderId, String newStatus);

    void updateOrderStatuss(Integer orderId, String status);

    Orders createOrder(Users user, String address, PaymentMethod paymentMethod, List<Integer> cartItemIds, Integer shippingUnitId);

    Orders getOrderByCode(String code);

    List<OrderManagerDTO> getOrders();

    List<OrderManagerDTO> getOrdersByStatus(String status);

    List<OrderManagerDTO> getOrdersByFilters(String status, LocalDate startDate, LocalDate endDate);

    List<OrderManagerDTO> getOrdersByFilters(String status, LocalDate startDate, LocalDate endDate, Integer storeId);

    void bulkUpdateOrderStatus(String currentStatus, String newStatus, LocalDate startDate, LocalDate endDate, Integer storeId);

    void bulkUpdateOrderStatus(String currentStatus, String newStatus, LocalDate startDate, LocalDate endDate);

    BigDecimal calculateRevenueForStore(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);

    List<Orders> findIncompleteOrdersByStore(Integer storeId);
}
