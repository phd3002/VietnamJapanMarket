package com.ecommerce.g58.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private String productName;
    private String thumbnail;
    private BigDecimal price;

    // Constructor
    public ProductDTO(String productName, String thumbnail, BigDecimal price) {
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public ProductDTO() {
    }

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}