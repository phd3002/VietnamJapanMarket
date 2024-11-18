package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoriesService categoriesService;

    @Test
    @WithMockUser(username = "lequyet180902@gmail.com")
    public void testShowHomePage_LoggedIn() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        when(categoriesService.getAllCategories()).thenReturn(Collections.emptyList());
        when(productService.getProductDetails()).thenReturn(Collections.emptyList());
        when(productService.getSearchProduct()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/homepage"))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"))
                .andExpect(model().attribute("isLoggedIn", true))
                .andExpect(model().attribute("username", "lequyet180902@gmail.com"))
                .andExpect(model().attribute("productDetails", Collections.emptyList()))
                .andExpect(model().attribute("searchProduct", Collections.emptyList()))
                .andExpect(model().attribute("categories", Collections.emptyList()));
    }

    @Test
    public void testShowHomePage_NotLoggedIn() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
        when(categoriesService.getAllCategories()).thenReturn(Collections.emptyList());
        when(productService.getProductDetails()).thenReturn(Collections.emptyList());
        when(productService.getSearchProduct()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/homepage"))
                .andExpect(status().isOk())
                .andExpect(view().name("homepage"))
                .andExpect(model().attribute("isLoggedIn", false))
                .andExpect(model().attribute("productDetails", Collections.emptyList()))
                .andExpect(model().attribute("searchProduct", Collections.emptyList()))
                .andExpect(model().attribute("categories", Collections.emptyList()));
    }
}