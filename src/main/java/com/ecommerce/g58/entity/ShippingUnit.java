package com.ecommerce.g58.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_unit")
public class ShippingUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "unit_contact")
    private Integer unitContact;

    @Column(name = "unit_address")
    private String unitAddress;

    @Column(name = "unit_phone")
    private String unitPhone;

    @Column(name = "shipping_revenue", nullable = false)
    private BigDecimal shippingRevenue;
}
