package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

@Getter
@Setter
public class ProductDetailDTO {
    // Product table fields
    private Integer productId;
    private String productName;
    private String productDescription;
    private Integer price;
    private Float weight;
    private Integer categoryId;
    private Integer storeId;
    private String storeMail;

    // ProductVariation table fields
    private Integer variationId;
    private String size;
    private String color;
    private Integer stock;

    // ProductImage table fields
    private Integer imageId;
    private String thumbnail;
    private String image1;
    private String image2;
    private String image3;
    private String image4;

    // Constructors
    public ProductDetailDTO() {}

    public ProductDetailDTO(Integer productId, String productName, String productDescription, Integer price, Float weight,
                            Integer variationId, String sizeName, String colorName, Integer stock, String thumbnail, Integer storeId, String storeMail) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.weight = weight;
        this.variationId = variationId;
        this.size = sizeName;
        this.color = colorName;
        this.stock = stock;
        this.thumbnail = thumbnail;
        this.storeId = storeId;
        this.storeMail = storeMail;
    }


    public ProductDetailDTO(Integer productId, String productName, String productDescription,
                            Integer price, Float weight, Integer variationId, String size,
                            String color, Integer stock, String thumbnail,
                            String image1, String image2, String image3, String image4, Integer storeId) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.weight = weight;
        this.variationId = variationId;
        this.size = size;
        this.color = color;
        this.stock = stock;
        this.thumbnail = thumbnail;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.storeId = storeId;
    }

    public ProductDetailDTO(Integer productId, String productName, Integer price, Integer categoryId, Integer storeId, String size, String color, Integer stock, Integer imageId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.size = size;
        this.color = color;
        this.stock = stock;
        this.imageId = imageId;
    }

    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(price));
    }
}
