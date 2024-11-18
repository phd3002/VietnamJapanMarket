package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Page<OrdersDTO> getOrdersByUserIdAndStatus(Integer userId, String status, Pageable pageable);

    List<OrderManagerDTO> getOrdersForStore(Integer userId);

    List<OrderManagerDTO> getOrdersByStoreId(Integer storeId);

    void updateOrderStatus(Orders orderId, String newStatus);

    void updateOrderStatuss(Integer orderId, String status);

    Orders createOrder(Users user, String address, PaymentMethod paymentMethod, List<Integer> cartItemIds);

    List<OrderManagerDTO> getOrders();
}
