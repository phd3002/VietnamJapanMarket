package com.ecommerce.g58.entity;

import lombok.*;
import jakarta.persistence.*;


import javax.management.relation.Role;
import java.time.LocalDateTime;
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
    private Long storeId;

    @Column(nullable = false)
    private String storeName;

    private String storeDescription;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users owner;
}
