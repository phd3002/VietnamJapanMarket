package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Stores storeId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories categoryId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "weight")
    private float weight;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "product_code", length = 10)
    private String productCode;
    @Column(columnDefinition="boolean DEFAULT true")
    private boolean visible;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "productId", fetch = FetchType.LAZY)
    private List<ProductVariation> productVariations;

    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(price));
    }
}

