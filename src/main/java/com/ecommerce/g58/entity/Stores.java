package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
public class Stores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_description")
    private String storeDescription;

    @Column(name = "picture_url")
    private String pictureUrl;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users ownerId;

    @Column(name = "store_revenue", nullable = false)
    private BigDecimal storeRevenue;

    @OneToOne
    @JoinColumn(name = "country_id")
    private Countries country;

    @OneToMany(mappedBy = "storeId")
    private List<Products> products;
}
