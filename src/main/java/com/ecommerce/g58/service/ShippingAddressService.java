package com.ecommerce.g58.service;

import com.ecommerce.g58.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ShippingAddressService {

    @Autowired
    private OrderRepository ordersRepository;

    // thay đổi địa chỉ giao hàng
    @Transactional
    public void updateShippingAddress(Integer orderId, String newAddress) {
        ordersRepository.updateShippingAddressByOrderId(orderId, newAddress);
    }
}