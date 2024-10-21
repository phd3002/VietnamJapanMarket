package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}