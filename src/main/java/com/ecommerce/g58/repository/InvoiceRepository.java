package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Invoice;
import com.ecommerce.g58.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Invoice findInvoiceByOrderId(Orders orders);
    Invoice findInvoiceByOrderId_OrderId(Integer orderId);
}
