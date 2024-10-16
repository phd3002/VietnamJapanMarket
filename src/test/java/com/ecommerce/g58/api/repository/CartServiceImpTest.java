package com.ecommerce.g58.api.repository;


import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.implementation.CartServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {CartServiceImp.class})
public class CartServiceImpTest {

    @Autowired
    private CartServiceImp cartService;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductVariationRepository productVariationRepository;

    private Users user;
    private Cart cart;
    private Products product;
    private ProductVariation productVariation;
    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("lequyet180902@gmail.com");

        cart = new Cart();
        cart.setCartId(1);
        cart.setUser(user);

        product = new Products();
        product.setProductId(1);
        product.setProductName("phone");

        productVariation = new ProductVariation();
        productVariation.setVariationId(1);
        productVariation.setProductId(product);

        cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setCart(cart);
        cartItem.setProductId(product);
        cartItem.setVariationId(productVariation);
        cartItem.setQuantity(1);
        cartItem.setPrice(100);
    }

    @Test
    public void testGetOrCreateCart() {
        when(cartRepository.findByUser(user)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart createdCart = cartService.getOrCreateCart(user);
        assertNotNull(createdCart);
        assertEquals(user, createdCart.getUser());
    }

    @Test
    public void testAddToCart() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productVariationRepository.findById(1)).thenReturn(Optional.of(productVariation));
        when(cartItemRepository.findByCartIdAndVariation(1, productVariation)).thenReturn(null);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        cartService.addToCart(cart, 1, 1, "phone", 1, 1, 100);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertNotNull(cartItem);
        assertEquals(cart, cartItem.getCart());
        assertEquals(1, cartItem.getQuantity());
        assertEquals(100, cartItem.getPrice());
    }

//    @Test
//    public void testGetTotalQuantityByUser() {
//        when(cartRepository.findByUser(user)).thenReturn(cart);
//        when(cartItemRepository.findByCart_CartId(1)).thenReturn(List.of(cartItem));
//
//        int totalQuantity = cartService.getTotalQuantityByUser(user);
//        assertEquals(1, totalQuantity);
//    }

    @Test
    public void testUpdateCartItemQuantity() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));

        cartService.updateCartItemQuantity(1, 2);

        assertEquals(2, cartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

}