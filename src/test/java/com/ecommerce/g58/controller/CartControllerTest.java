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
    private CartController cartController;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private CartItemService cartItemService;

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Users user;

    @Mock
    private Cart cart;

    @Mock
    private Model model;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItem cartItem;

    @Mock
    private ProductVariation productVariation;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private Logger logger;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // testAddToCart_UserNotAuthenticated tc1
    @Test
    public void testAddToCart_UserNotAuthenticatedtc1() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/sign-in", result);
    }

    // testAddToCart_ProductAddedSuccessfully tc2
    @Test
    public void testAddToCart_ProductAddedSuccessfullytc2() {
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
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng của bạn");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc3
    @Test
    public void testAddToCart_ProductNotFoundtc3() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductQuantitytc4
    @Test
    public void testAddToCart_ProductQuantitytc4() {
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
        String result = cartController.addToCart(1, 1, 0, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, 0, cart);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductQuantitytc5
    @Test
    public void testAddToCart_ProductQuantitytc5() {
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
        String result = cartController.addToCart(1, 1, -1, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, -1, cart);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "");
        assertEquals("redirect:/product-detail/1", result);
    }

    // ttestAddToCart_ProductQuantitytc6
    @Test
    public void testAddToCart_ProductQuantitytc6() {
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
        String result = cartController.addToCart(1, 1, 999999999, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, 999999999, cart);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Sản phẩm đã được thêm vào giỏ hàng của bạn");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc7
    @Test
    public void testAddToCart_ProductNotFoundtc7() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc8
    @Test
    public void testAddToCart_ProductNotFoundtc8() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc9
    @Test
    public void testAddToCart_ProductNotFoundtc9() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 0, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc10
    @Test
    public void testAddToCart_ProductNotFoundtc10() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, -1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc11
    @Test
    public void testAddToCart_ProductNotFoundtc11() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 999999999, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc12
    @Test
    public void testAddToCart_ProductNotFoundtc12() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 0, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc13
    @Test
    public void testAddToCart_ProductNotFoundtc13() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, -1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc14
    @Test
    public void testAddToCart_ProductNotFoundtc14() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 999999999, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc15
    @Test
    public void testAddToCart_ProductNotFoundtc15() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc16
    @Test
    public void testAddToCart_ProductNotFoundtc16() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc17
    @Test
    public void testAddToCart_ProductNotFoundtc17() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không thể thêm sản phẩm vào giỏ hàng");
        assertEquals("redirect:/product-detail/1", result);
    }


    // testAddToCart_ExceptionThrown tc4
    @Test
    public void testAddToCart_ExceptionThrown() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        doThrow(new RuntimeException("Test Exception")).when(cartService).addProductToCart(user, productDetail, 1, cart);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Đã có lỗi xảy ra khi thêm vào giỏ hảng");
        assertEquals("redirect:/product-detail/1", result);
    }

    //--------------------------------------------------------------------------------------------------------------
    // testGetCartItems_UserNotAuthenticated tc1
    @Test
    public void testGetCartItems_UserNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        String result = cartController.getCartItems(model);
        assertEquals("redirect:/sign-in", result);
    }

    // testGetCartItems_EmptyCart tc2
    @Test
    public void testGetCartItems_EmptyCart() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(user.getUserId()).thenReturn(1);
        when(cartItemService.getCartItemsByUserId(1)).thenReturn(Collections.emptyList());
        String result = cartController.getCartItems(model);
        verify(model, times(1)).addAttribute("message", "Your cart is empty.");
        assertEquals("cart-detail", result);
    }

    //-----------------------------------------------------------------------------------------------------------------
    // testGetCartItems_Success tc1
    @Test
    public void testRemoveCartItem_Success() {
        Integer cartItemId = 1;
        String result = cartController.removeCartItem(cartItemId);
        verify(cartService, times(1)).removeCartItem(cartItemId);
        assertEquals("redirect:/cart-items", result);
    }

    //----------------------------------------------------------------------------------------------------------------

    // testGetCartItems_Success tc1
    @Test
    public void testUpdateCartQuantity_Success() {
        Integer cartItemId = 1;
        Integer quantity = 2;
        int productStock = 5;
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        when(cartItem.getVariationId()).thenReturn(productVariation);
        when(productVariation.getStock()).thenReturn(productStock);
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        verify(cartItemService, times(1)).updateCartItemQuantity(cartItemId, quantity);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Quantity updated successfully");
        assertEquals("redirect:/cart-items", result);
    }


    //--------------------------------------------------------------------------------------------------------------
}