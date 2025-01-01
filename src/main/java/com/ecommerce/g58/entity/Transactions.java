package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@Getter
@Setter
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWalletId;

    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWalletId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "previous_balance")
    private long previousBalance;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "description")
    private String description;

    @Column(name = "is_refund")
    private String isRefund;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "payment_type")
    private String paymentType;
}
