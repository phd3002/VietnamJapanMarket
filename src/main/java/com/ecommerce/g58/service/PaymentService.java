package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Payment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService {
    List<Payment> getAllPaymentMethods();
    Payment getPaymentMethodById(int methodId);
    void savePaymentMethod(Payment paymentMethod);
    void deletePaymentMethod(int methodId);
}
