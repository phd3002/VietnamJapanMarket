package com.ecommerce.g58.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

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

    // Getter for formatted total amount
    public String getFormattedTotalAmount() {
        return formatCurrency(totalAmount);
    }

    // Getter for formatted tax
    public String getFormattedTax() {
        return formatCurrency(tax);
    }

    // Getter for formatted shipping fee
    public String getFormattedShippingFee() {
        return formatCurrency(shippingFee);
    }


    public String getFormatedRemainingBalance() {
        return formatCurrency(remainingBalance);
    }

    public String getFormatedDeposit() {
        return formatCurrency(deposit);
    }



}
