package com.ecommerce.g58.controller;

import com.ecommerce.g58.config.TestSecurityConfig;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CartItemRepository cartItemRepository;

    private Users user;
    private Cart cart;
    private ProductDetailDTO productDetail;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("lequyet180902@gmail.com");

        cart = new Cart();
        cart.setCartId(1);
        cart.setUser(user);

        productDetail = new ProductDetailDTO();
        // Initialize productDetail with necessary fields
    }

    @Test
    @WithMockUser(username = "lequyet180902@gmail.com")
    public void testAddToCart_Success() throws Exception {
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(anyInt(), anyInt())).thenReturn(productDetail);

        mockMvc.perform(post("/add_to_cart")
                        .param("productId", "1")
                        .param("variationId", "1")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**"));

        verify(cartService, times(1)).addProductToCart(user, productDetail, 1, cart);
    }
}