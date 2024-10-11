package com.ecommerce.g58.dto;


public class ProductDTO {
    private String productName;
    private String thumbnail;
    private Integer price;

    // Constructor
    public ProductDTO(String productName, String thumbnail, Integer price) {
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
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}