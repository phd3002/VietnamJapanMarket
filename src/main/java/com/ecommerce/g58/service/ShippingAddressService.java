package com.ecommerce.g58.service;

import com.ecommerce.g58.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ShippingAddressService {

    @Autowired
    private OrderRepository ordersRepository;

    // Modify (update) shipping address
    @Transactional
    public void updateShippingAddress(Integer orderId, String newAddress) {
        ordersRepository.updateShippingAddressByOrderId(orderId, newAddress);
    }

    // View saved shipping address by order ID
    public String getShippingAddress(Integer orderId) {
        return ordersRepository.findShippingAddressByOrderId(orderId);
    }

    // Optional: Remove shipping address by setting it to null (if applicable)
    @Transactional
    public void deleteShippingAddress(Integer orderId) {
        ordersRepository.updateShippingAddressByOrderId(orderId, null);
    }
}