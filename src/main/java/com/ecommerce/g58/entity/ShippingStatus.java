package com.ecommerce.g58.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_status")
public class ShippingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "previous_status", nullable = false)
    private String previousStatus;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "reason")
    private String reason;

     @Column(name = "previous_status")
    private String previous_status;


}
