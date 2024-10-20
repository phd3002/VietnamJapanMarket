package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wishlist;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private ProductService productService;

    private Users user;
    private ProductDetailDTO productDetail;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("lequyet180902@gmail.com");

        productDetail = new ProductDetailDTO();
        // Initialize productDetail with necessary fields
    }

    @Test
    @WithMockUser(username = "lequyet180902@gmail.com")
    public void testAddToWishlist_Success() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(productService.getProductDetailByProductIdAndVariationId(anyInt(), anyInt())).thenReturn(productDetail);

        mockMvc.perform(post("/add_to_wishlist")
                        .param("productId", "1")
                        .param("variationId", "1")
                        .header("Referer", "/product-detail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product-detail"))
                .andExpect(flash().attributeExists("message"));

        verify(wishlistService, times(1)).addProductToWishlist(user, productDetail);
    }

    @Test
    @WithMockUser(username = "lequyet180902@gmail.com")
    public void testAddToWishlist_Failure() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(productService.getProductDetailByProductIdAndVariationId(anyInt(), anyInt())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/add_to_wishlist")
                        .param("productId", "1")
                        .param("variationId", "1")
                        .header("Referer", "/product-detail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product-detail"))
                .andExpect(flash().attributeExists("error"));

        verify(wishlistService, never()).addProductToWishlist(any(Users.class), any(ProductDetailDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testGetWishlist_WithItems() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(wishlistService.getUserWishlist(anyInt())).thenReturn(Collections.singletonList(new Wishlist()));

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist"))
                .andExpect(model().attributeExists("wishlist"));
    }

    @Test
    @WithMockUser(username = "lequyet180902@gmail.com")
    public void testGetWishlist_Empty() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(wishlistService.getUserWishlist(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist"))
                .andExpect(model().attributeExists("message"));
    }
}