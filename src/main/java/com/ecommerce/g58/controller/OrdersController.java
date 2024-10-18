//package com.ecommerce.g58.controller;
//
//import com.ecommerce.g58.entity.OrderDetails;
//import com.ecommerce.g58.entity.Orders;
//import com.ecommerce.g58.service.OrdersService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//public class OrdersController {
//
//    @Autowired
//    private OrdersService orderService;
//
//    @GetMapping("/{userId}")
//    public List<Orders> getUserOrders(@PathVariable Long userId) {
//        return orderService.getOrdersByUserId(userId);
//    }
//
//    @GetMapping("/detail/{orderId}")
//    public Orders getOrderById(@PathVariable Long orderId) {
//        return orderService.getOrderById(orderId);
//    }
//
//    @GetMapping("/{orderId}/details")
//    public List<OrderDetails> getOrderDetails(@PathVariable Long orderId) {
//        return orderService.getOrderDetails(orderId);
//    }
//}
