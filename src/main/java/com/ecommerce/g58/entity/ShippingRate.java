package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_rate")
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Integer rateId;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private ShippingUnit unitId;

    @Column(name = "weight", nullable = false)
    private float weight;

    @ManyToOne
    @JoinColumn(name = "country_from")
    private Countries countryFrom;

    @ManyToOne
    @JoinColumn(name = "country_to")
    private Countries countryTo;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;
}
