package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.enums.PaymentMethod;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailAsync(String to, String subject, String body);
    void sendTransactionMailAsync(Users user, Transactions transaction, Long amount);
    void sendCheckoutCompleteEmail(Users user, Stores store, Invoice invoice, PaymentMethod paymentMethod);
    void sendOrderStatusChangeEmail(Users user, Invoice invoice, Orders order, String status);
    void sendWarningEmail(Users user, Orders order, ShippingStatus status);
    void sendOrderCancellationEmail(Users user, Orders order);
    void sendOrderConfirmationEmail(Users user, Orders order);
    void sendOrderDenialEmail(Users user, Orders order);
}
