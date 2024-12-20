package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private ProductVariation variationId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private Integer price;
}
