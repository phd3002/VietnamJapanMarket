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
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "variation_id")
    private ProductVariation variationId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private Integer price;

    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(price));
    }
    public String getTotalPriceFormated() {
        return formatCurrency(BigDecimal.valueOf((long) price * quantity));
    }

}
