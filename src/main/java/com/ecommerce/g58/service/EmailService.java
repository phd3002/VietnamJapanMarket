package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Invoice;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Transactions;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.PaymentMethod;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendTransactionMailAsync(Users user, Transactions transaction, Long amount);
    void sendCheckoutCompleteEmail(Users user, Stores store, Invoice invoice, PaymentMethod paymentMethod);
}
