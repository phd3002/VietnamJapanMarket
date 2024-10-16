package com.ecommerce.g58.api.repository;

import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.implementation.CartItemServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {CartItemServiceImp.class})
public class CartItemServiceImpTest {

    @Autowired
    private CartItemServiceImp cartItemService;

    @MockBean
    private CartItemRepository cartItemRepository;

    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setQuantity(1);
        cartItem.setPrice(100);
    }

    @Test
    public void testGetCartItemsByUserId() {
        when(cartItemRepository.findByUserId(1)).thenReturn(List.of(cartItem));

        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(1);
        assertNotNull(cartItems);
        assertEquals(1, cartItems.size());
        assertEquals(1, cartItems.get(0).getCartItemId());
    }

    @Test
    public void testUpdateCartItemQuantity() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));
        cartItemService.updateCartItemQuantity(1, 2);
        assertEquals(2, cartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    public void testRemoveCartItemById() {
        doNothing().when(cartItemRepository).deleteById(1);

        cartItemService.removeCartItemById(1);

        verify(cartItemRepository, times(1)).deleteById(1);
    }
}