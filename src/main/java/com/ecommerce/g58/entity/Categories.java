package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_image")
    private String categoryImage;
}
