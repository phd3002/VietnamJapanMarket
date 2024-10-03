package com.ecommerce.g58.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "additional_fee")
    private BigDecimal additionalFee;

    @Column(name = "deposit")
    private BigDecimal deposit;

    @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;
}
