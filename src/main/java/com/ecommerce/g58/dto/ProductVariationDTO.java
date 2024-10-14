package com.ecommerce.g58.dto;

import com.ecommerce.g58.entity.Products;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariationDTO {
    private Integer productId;
    private String productName;
    private String categoryName;
    private Integer variationId;
    private String size;
    private String color;
    private Integer stock;

    // Add image fields
    private String thumbnail;
    private String image1;
    private String image2;
    private String image3;
    private String image4;

    private int price;
    private double discount;

    public ProductVariationDTO(Integer productId, String thumbnail, String image1, String image2, String image3, String image4, String categoryName, String productName, String color, String size, int price, int stock) {
        this.productId = productId;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.categoryName = categoryName;
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }

    public ProductVariationDTO(Integer productId, String thumbnail, String image1, String image2, String image3, String image4, String categoryName, String productName, Integer price, Integer stock) {
        this.productId = productId;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.categoryName = categoryName;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
    }


}

