package com.ecommerce.g58.controller;


import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private Users user;
    private Cart cart;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("lequyet180902@gmail.com");

        cart = new Cart();
        cart.setCartId(1);
        cart.setUser(user);
    }

    @Test
    public void testGetCartByUser() throws Exception {
        when(cartService.getOrCreateCart(user)).thenReturn(cart);

        mockMvc.perform(get("/cart")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.user.userId").value(1))
                .andExpect(jsonPath("$.user.email").value("lequyet180902@gmail.com"));

        verify(cartService, times(1)).getOrCreateCart(user);
    }

    @Test
    public void testAddToCart() throws Exception {
        mockMvc.perform(post("/cart/add")
                        .param("cartId", "1")
                        .param("productId", "1")
                        .param("variationId", "1")
                        .param("productName", "phone")
                        .param("quantity", "1")
                        .param("price", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService, times(1)).addToCart(any(Cart.class), eq(1), eq(1), eq("phone"), eq(1), eq(1), eq(100));
    }
}
