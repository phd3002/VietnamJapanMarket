package com.ecommerce.g58.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
public class ProductDetailDTO {

    private Integer productId;
    private String productName;
    private int price;
    private String thumbnail;

    // Các trường hình ảnh bổ sung
    private String image1;
    private String image2;
    private String image3;
    private String image4;

    // Màu sắc và kích thước
    private List<String> colors;
    private List<String> sizes;


//    public ProductDetailDTO(Integer productId, String productName, int price, String thumbnail, String image1, String image2, String image3, String image4, List<String> colors, List<String> sizes) {
//    }
}
