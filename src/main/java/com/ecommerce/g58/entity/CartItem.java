package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cartId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private ProductVariation variationId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
