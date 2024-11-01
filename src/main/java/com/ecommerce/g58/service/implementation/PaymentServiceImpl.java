package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Payment;
import com.ecommerce.g58.repository.PaymentRepository;
import com.ecommerce.g58.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getAllPaymentMethods() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentMethodById(int methodId) {
        return paymentRepository.findById(methodId).orElse(null);
    }

    @Override
    public void savePaymentMethod(Payment paymentMethod) {
        paymentRepository.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(int methodId) {
        paymentRepository.deleteById(methodId);
    }
}
