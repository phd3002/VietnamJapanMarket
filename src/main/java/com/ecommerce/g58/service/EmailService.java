package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendTransactionMailAsync(Users user, Transactions transaction, Long amount);
}
