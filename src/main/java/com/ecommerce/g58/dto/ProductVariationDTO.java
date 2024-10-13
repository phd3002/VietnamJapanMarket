package com.ecommerce.g58.dto;

import com.ecommerce.g58.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariationDTO {
    // Getters and Setters
    private Products productId;
    private Integer variationId;
    private String size;
    private String color;
    private int stock;

    // Add image fields
    private String thumbnail;
    private String image1;
    private String image2;
    private String image3;
    private String image4;

    private int price;

    // Constructors

    public ProductVariationDTO() {}

    public ProductVariationDTO(Integer variationId, String thumbnail) {
        this.variationId = variationId;
        this.thumbnail = thumbnail;
    }

    public ProductVariationDTO(Integer variationId, String size, String color, String thumbnail, String image1, String image2, String image3, String image4) {
        this.variationId = variationId;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
    }

    public ProductVariationDTO(Integer variationId, String thumbnail, String image1, String image2, String image3, String image4) {
        this.variationId = variationId;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
    }

    public ProductVariationDTO(Products productId, Integer variationId, String size, String color, String image1, String image2, String thumbnail, String image3, String image4) {
        this.productId = productId;
        this.variationId = variationId;
        this.size = size;
        this.color = color;
        this.image1 = image1;
        this.image2 = image2;
        this.thumbnail = thumbnail;
        this.image3 = image3;
        this.image4 = image4;
    }

    public ProductVariationDTO(Products productId, Integer variationId, String thumbnail, String image1, String image2, String image3, String image4) {
        this.productId = productId;
        this.variationId = variationId;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
    }

    public ProductVariationDTO(Integer variationId, String size, String color, String thumbnail, String image1, String image2, String image3, String image4, int price) {
        this.variationId = variationId;
        this.size = size;
        this.color = color;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.price = price;
    }
}

