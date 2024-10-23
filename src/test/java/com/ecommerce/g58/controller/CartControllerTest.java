package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.CartItemRepository;
import com.ecommerce.g58.service.CartItemService;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.utils.SecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController; // Inject the UserController

    @Mock
    private UserService userService; // Mock the UserService

    @Mock
    private ProductService productService; // Mock the ProductService

    @Mock
    private CartItemService cartItemService; // Mock the CartItemService

    @Mock
    private CartService cartService; // Mock the CartService

    @Mock
    private Authentication authentication; // Mock the Authentication

    @Mock
    private HttpServletRequest request; // Mock HttpServletRequest

    @Mock
    private RedirectAttributes redirectAttributes; // Mock RedirectAttributes

    @Mock
    private Users user; // Mock User entity

    @Mock
    private Cart cart; // Mock Cart entity

    @Mock
    private Model model; // Mock the Model

    @Mock
    private CartItemRepository cartItemRepository; // Mock the CartItemRepository

    @Mock
    private CartItem cartItem; // Mock the CartItem

    @Mock
    private ProductVariation productVariation; // Mock the ProductVariation (for stock check)

    @Mock
    private SecurityUtils securityUtils; // Mock the SecurityUtils

    @Mock
    private Logger logger; // Mock the logger

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks before each test
    }

    // Test: user is not authenticated (redirect to sign-in)
    @Test
    public void testAddToCart_UserNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);

        // Assert redirect to sign-in page
        assertEquals("redirect:/sign-in", result);
    }

    // Test: user is authenticated, product added successfully
    @Test
    public void testAddToCart_ProductAddedSuccessfully() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, 1, cart);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Product successfully added to your cart!");
        assertEquals("redirect:/product-detail/1", result);
    }

    // Test: product not found, show error message
    @Test
    public void testAddToCart_ProductNotFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);

        // Verify the error message was added
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Failed to add product to cart. Product not found.");

        // Assert the correct redirect
        assertEquals("redirect:/product-detail/1", result);
    }

    // Test: exception is thrown while adding product to cart
    @Test
    public void testAddToCart_ExceptionThrown() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock user and cart retrieval
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);

        // Mock product detail retrieval
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);

        // Simulate exception when adding product to cart
        doThrow(new RuntimeException("Test Exception")).when(cartService).addProductToCart(user, productDetail, 1, cart);

        // Mock referer header
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");

        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);

        // Verify the error message was added
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Error adding product to cart. Please try again.");

        // Assert the correct redirect
        assertEquals("redirect:/product-detail/1", result);
    }

    //--------------------------------------------------------------------------------------------------------------
    // Test: user is not authenticated (redirect to sign-in)
    @Test
    public void testGetCartItems_UserNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String result = cartController.getCartItems(model);

        // Assert redirect to sign-in page
        assertEquals("redirect:/sign-in", result);
    }

    // Test: cart is empty
    @Test
    public void testGetCartItems_EmptyCart() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock user data
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(user.getUserId()).thenReturn(1);

        // Mock empty cart items
        when(cartItemService.getCartItemsByUserId(1)).thenReturn(Collections.emptyList());

        String result = cartController.getCartItems(model);

        // Verify message added to model
        verify(model, times(1)).addAttribute("message", "Your cart is empty.");

        // Assert the correct view is returned
        assertEquals("cart-detail", result);
    }

    // Test: cart has items
    @Test
    public void testGetCartItems_HasItems() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock user data
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(user.getUserId()).thenReturn(1);

        // Mock cart items
        Products product1 = mock(Products.class);
        Products product2 = mock(Products.class);
        Stores store1 = mock(Stores.class);
        Stores store2 = mock(Stores.class);

        // Set up mock products and stores
        when(product1.getStoreId()).thenReturn(store1);
        when(product1.getPrice()).thenReturn(100);
        when(product2.getStoreId()).thenReturn(store2);
        when(product2.getPrice()).thenReturn(200);

        CartItem cartItem1 = mock(CartItem.class);
        CartItem cartItem2 = mock(CartItem.class);
        when(cartItem1.getProductId()).thenReturn(product1);
        when(cartItem1.getQuantity()).thenReturn(2);
        when(cartItem2.getProductId()).thenReturn(product2);
        when(cartItem2.getQuantity()).thenReturn(1);

        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);
        when(cartItemService.getCartItemsByUserId(1)).thenReturn(cartItems);

        String result = cartController.getCartItems(model);

        // Verify items grouped by store
        verify(model, times(1)).addAttribute(eq("cartItemGroupedByStore"), any(Map.class));

        // Verify total order price calculation
        verify(model, times(1)).addAttribute("totalOrderPrice", 400);

        // Assert the correct view is returned
        assertEquals("cart-detail", result);
    }

//-----------------------------------------------------------------------------------------------------------------
    // Test: remove cart item successfully
    @Test
    public void testRemoveCartItem_Success() {
        // Define cartItemId
        Integer cartItemId = 1;

        // Execute the method
        String result = cartController.removeCartItem(cartItemId);

        // Verify that the removeCartItem method in CartService is called
        verify(cartService, times(1)).removeCartItem(cartItemId);

        // Assert that the correct redirection is returned
        assertEquals("redirect:/cart-items", result);
    }

    //----------------------------------------------------------------------------------------------------------------
    // Test: Update cart quantity successfully
    @Test
    public void testUpdateCartQuantity_Success() {
        // Define cart item details
        Integer cartItemId = 1;
        Integer quantity = 2;
        int productStock = 5;

        // Mock the cart item and variation with stock
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        when(cartItem.getVariationId()).thenReturn(productVariation);
        when(productVariation.getStock()).thenReturn(productStock);

        // Execute the method
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);

        // Verify the quantity update and flash message
        verify(cartItemService, times(1)).updateCartItemQuantity(cartItemId, quantity);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Quantity updated successfully");

        // Assert the correct redirection
        assertEquals("redirect:/cart-items", result);
    }

    // Test: Requested quantity exceeds stock
    @Test
    public void testUpdateCartQuantity_ExceedsStock() {
        // Define cart item details
        Integer cartItemId = 1;
        Integer quantity = 10; // Exceeds available stock
        int productStock = 5;

        // Mock the cart item and variation with stock
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        when(cartItem.getVariationId()).thenReturn(productVariation);
        when(productVariation.getStock()).thenReturn(productStock);

        // Execute the method
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);

        // Verify no quantity update and correct flash message
        verify(cartItemService, times(0)).updateCartItemQuantity(cartItemId, quantity);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Error updating quantity: Exceeds stock limit");

        // Assert the correct redirection
        assertEquals("redirect:/cart-items", result);
    }

    // Test: Exception occurs while updating quantity
    @Test
    public void testUpdateCartQuantity_Exception() {
        // Define cart item details
        Integer cartItemId = 1;
        Integer quantity = 2;
        int productStock = 5;

        // Mock the cart item and variation with stock
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        when(cartItem.getVariationId()).thenReturn(productVariation);
        when(productVariation.getStock()).thenReturn(productStock);

        // Mock an exception during cart item update
        doThrow(new RuntimeException("Update failed")).when(cartItemService).updateCartItemQuantity(cartItemId, quantity);

        // Execute the method
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);

        // Verify the error message and exception handling
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Error updating quantity");

        // Assert the correct redirection
        assertEquals("redirect:/cart-items", result);
    }

    //--------------------------------------------------------------------------------------------------------------
}