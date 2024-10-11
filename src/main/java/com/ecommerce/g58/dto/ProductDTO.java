package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

//import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Integer productId;
    private Integer variationId;
    private String productName;
    private String thumbnail;
    private Integer price;
    private Integer imageId;


    // Constructor
    public ProductDTO(String productName, String thumbnail, Integer price) {
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.price = price;
    }

//    public ProductDTO(String productName, String thumbnail, Integer price, Integer productId, Integer variationId, Integer storeId, Integer categoryId, String productDescription, Integer imageId) {
//        this.productName = productName;
//        this.thumbnail = thumbnail;
//        this.price = price;
//        this.productId = productId;
//        this.variationId = variationId;
//        this.storeId = storeId;
//        this.categoryId = categoryId;
//        this.productDescription = productDescription;
//        this.imageId = imageId;
//    }

    public ProductDTO() {
    }

}
