package com.ecommerce.g58.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "commission_fee")
    private BigDecimal commissionFee;

    @Column(name = "additional_fee")
    private BigDecimal additionalFee;

    @Column(name = "deposit")
    private BigDecimal deposit;

    @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Countries countryId;

    @ManyToOne
    @JoinColumn(name = "rate_id")
    private ShippingRate rateId;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private ShippingUnit unitId;
}
