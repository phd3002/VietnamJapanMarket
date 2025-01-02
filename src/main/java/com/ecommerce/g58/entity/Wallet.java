package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Integer walletId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public String getBalanceFormated() {
        return formatCurrency(BigDecimal.valueOf(balance));
    }
}
