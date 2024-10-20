package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Color;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Products product;
    private ProductDetailDTO productDetail;
    private Color color;

    @BeforeEach
    public void setUp() {
        product = new Products();
        product.setProductId(1);
        product.setProductName("Test Product");

        productDetail = new ProductDetailDTO();
        productDetail.setProductId(1);
        productDetail.setProductName("Test Product Detail");

        color = new Color();
        color.setColorId(1);
        color.setColorName("Red");
    }

    @Test
    public void testProductList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/product-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    public void testShowProductDetail() throws Exception {
        when(productService.getAvailableColors(anyInt())).thenReturn(Collections.singletonList(color));
        when(productService.getProductDetailByProductIdAndColorId(anyInt(), anyInt())).thenReturn(productDetail);
        when(productService.getAvailableSizesByProductIdAndColorId(anyInt(), anyInt())).thenReturn(Collections.singletonList("M"));

        mockMvc.perform(get("/product-detail/1").param("colorId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-detail"))
                .andExpect(model().attributeExists("productDetail"))
                .andExpect(model().attributeExists("availableColors"))
                .andExpect(model().attributeExists("availableSizes"))
                .andExpect(model().attributeExists("selectedSize"));
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        when(productService.getProductsByCategory(anyLong())).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/products").param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"));
    }
}