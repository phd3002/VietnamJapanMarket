package com.ecommerce.g58.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Integer sizeId;

    @Column(name = "size_name", nullable = false)
    private String sizeName;
}
