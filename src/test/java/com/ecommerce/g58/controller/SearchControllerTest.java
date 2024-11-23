package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void testSearchProducts() throws Exception {
        String query = "test";
        ProductDTO productDTO = new ProductDTO();
        when(productService.searchProducts(query)).thenReturn(Collections.singletonList(productDTO));
        mockMvc.perform(get("/api/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}