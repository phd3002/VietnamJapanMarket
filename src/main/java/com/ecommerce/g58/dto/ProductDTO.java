package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

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
    private String description;
    private float weight;
    private List<ProductVariationDTO> productVariations;



    // Constructor
    public ProductDTO(Integer productId, String productName, String thumbnail, Integer price) {
       this.productId = productId;
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public ProductDTO(Integer productId, String productName, String thumbnail, int price) {
        this.productId = productId;
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.price = price;
    }


    public ProductDTO(Integer productId, String productName, String thumbnail, int price, String description) {
        this.productId = productId;
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.price = price;
        this.description = description;
    }


    public ProductDTO() {
    }
    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(price));
    }
}
