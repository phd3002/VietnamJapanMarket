//package com.ecommerce.g58.service;
//
//import com.ecommerce.g58.entity.OrderDetails;
//import com.ecommerce.g58.entity.Orders;
//import com.ecommerce.g58.repository.OrderDetailRepository;
//import com.ecommerce.g58.repository.OrderRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class OrdersService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderDetailRepository orderDetailRepository;
//
//    public List<Orders> getOrdersByUserId(Long userId) {
//        return orderRepository.findByUserId(userId);
//    }
//
//    //    public Orders getOrderById(Long orderId) {
////        return orderRepository.findById(orderId)
////                .orElseThrow(() -> new NewRuntimeException("Order not found!"));
////    }
//    public Orders getOrderById(Long orderId) {
//        return orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found!"));
//    }
//
//    public List<OrderDetails> getOrderDetails(Long orderId) {
//        return orderDetailRepository.findByOrderId(orderId);
//    }
//}
