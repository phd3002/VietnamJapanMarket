package com.ecommerce.g58.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestSellingDTO {
    private String productCode;
    private String productName;
    private Integer price;
    private Integer quantitySold;
    private String category;
}
