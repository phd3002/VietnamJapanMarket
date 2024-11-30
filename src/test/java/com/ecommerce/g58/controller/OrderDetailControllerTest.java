package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderDetailControllerTest {
    @InjectMocks
    private OrderDetailController orderDetailController;

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Test case 1
    @Test
    public void testGetOrderDetails_OrderDetailsPresent() {
        Long orderId = 1L;
        OrderDetailDTO orderDetail = new OrderDetailDTO();
        orderDetail.setOrderTotalPrice(500);
        orderDetail.setTotalAmount(3);
        orderDetail.setShippingFee(20);
        orderDetail.setPaymentMethod("Credit Card");
        orderDetail.setPaymentStatus("Paid");
        orderDetail.setShippingAddress("123 Main St");
        orderDetail.setShippingStatus("Shipped");
        orderDetail.setOrderCode("TRACK12345");
        orderDetail.setStoreName("Test Store");
        List<OrderDetailDTO> orderDetails = List.of(orderDetail);
        when(orderDetailService.getOrderDetails(orderId)).thenReturn(orderDetails);
        String result = orderDetailController.getOrderDetails(orderId, model);
        assertEquals("order-detail", result);
        verify(model, times(1)).addAttribute("orderDetails", orderDetails);
        verify(model, times(1)).addAttribute("orderId", orderId);
        verify(model, times(1)).addAttribute("orderTotalPrice", orderDetail.getOrderTotalPrice());
        verify(model, times(1)).addAttribute("totalAmount", orderDetail.getTotalAmount());
        verify(model, times(1)).addAttribute("shippingFee", orderDetail.getShippingFee());
        verify(model, times(1)).addAttribute("paymentMethod", orderDetail.getPaymentMethod());
        verify(model, times(1)).addAttribute("paymentStatus", orderDetail.getPaymentStatus());
        verify(model, times(1)).addAttribute("shippingAddress", orderDetail.getShippingAddress());
        verify(model, times(1)).addAttribute("shippingStatus", orderDetail.getShippingStatus());
        verify(model, times(1)).addAttribute("trackingNumber", orderDetail.getOrderCode());
        verify(model, times(1)).addAttribute("storeName", orderDetail.getStoreName());
    }

    @Test
    public void testGetOrderDetails_NoOrderDetails() {
        Long orderId = 2L;
        List<OrderDetailDTO> orderDetails = Collections.emptyList();
        when(orderDetailService.getOrderDetails(orderId)).thenReturn(orderDetails);
        String result = orderDetailController.getOrderDetails(orderId, model);
        assertEquals("order-detail", result);
        verify(model, times(1)).addAttribute("orderDetails", orderDetails);
        verify(model, never()).addAttribute(eq("orderTotalPrice"), any());
        verify(model, never()).addAttribute(eq("totalAmount"), any());
        verify(model, never()).addAttribute(eq("shippingFee"), any());
        verify(model, never()).addAttribute(eq("paymentMethod"), any());
        verify(model, never()).addAttribute(eq("paymentStatus"), any());
        verify(model, never()).addAttribute(eq("shippingAddress"), any());
        verify(model, never()).addAttribute(eq("shippingStatus"), any());
        verify(model, never()).addAttribute(eq("trackingNumber"), any());
        verify(model, never()).addAttribute(eq("storeName"), any());
    }


}