package com.ecommerce.g58.dto;

import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Products;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductWithVariationsDTO {
    // Getters and setters
    private Products product;
    private List<ProductVariation> productVariations;

    // Constructor
    public ProductWithVariationsDTO(Products product, List<ProductVariation> productVariations) {
        this.product = product;
        this.productVariations = productVariations;
    }

}

