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
import java.util.*;
import org.slf4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


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
        redirectAttributes = new RedirectAttributesModelMap();
    }

    // testAddToCart_UserNotAuthenticated tc1
    @Test
    public void testAddToCart_UserNotAuthenticatedtc1() {
        // Given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        assertEquals("redirect:/sign-in", result);
        assertEquals("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddToCart_ProductAddedSuccessfully tc2
    @Test
    public void testAddToCart_ProductAddedSuccessfullytc2() {
        // Given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 1, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, 1, cart);
        assertEquals("Sản phẩm đã được thêm vào giỏ hàng của bạn", redirectAttributes.getFlashAttributes().get("message"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc3
    @Test
    public void testAddToCart_ProductNotFoundtc3() {
        // Given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 1, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductQuantitytc4
    @Test
    public void testAddToCart_ProductQuantitytc4() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 0, redirectAttributes, request);
        verify(cartService, times(1)).addProductToCart(user, productDetail, 0, cart);
        assertEquals("Sản phẩm đã được thêm vào giỏ hàng của bạn", redirectAttributes.getFlashAttributes().get("message"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductQuantitytc5
    @Test
    public void testAddToCart_ProductQuantitytc5() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, -1, redirectAttributes, request);
        assertEquals("Sản phẩm đã được thêm vào giỏ hàng của bạn", redirectAttributes.getFlashAttributes().get("message"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // ttestAddToCart_ProductQuantitytc6
    @Test
    public void testAddToCart_ProductQuantitytc6() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        ProductDetailDTO productDetail = mock(ProductDetailDTO.class);
        when(productService.getProductDetailByProductIdAndVariationId(1, 1)).thenReturn(productDetail);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 1, 999999999, redirectAttributes, request);
        assertEquals("Sản phẩm đã được thêm vào giỏ hàng của bạn", redirectAttributes.getFlashAttributes().get("message"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc7
    @Test
    public void testAddToCart_ProductNotFoundtc7() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 1, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc8
    @Test
    public void testAddToCart_ProductNotFoundtc8() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 1, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc9
    @Test
    public void testAddToCart_ProductNotFoundtc9() {
        // Given
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 0, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }


    // testAddToCart_ProductNotFound tc10
    @Test
    public void testAddToCart_ProductNotFoundtc10() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, -1, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc11
    @Test
    public void testAddToCart_ProductNotFoundtc11() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 1)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 1, 999999999, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc12
    @Test
    public void testAddToCart_ProductNotFoundtc12() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 0, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }
    // testAddToCart_ProductNotFound tc13
    @Test
    public void testAddToCart_ProductNotFoundtc13() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, -1, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc14
    @Test
    public void testAddToCart_ProductNotFoundtc14() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(0, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(0, 0, 999999999, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc15
    @Test
    public void testAddToCart_ProductNotFoundtc15() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc16
    @Test
    public void testAddToCart_ProductNotFoundtc16() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
        assertEquals("redirect:/product-detail/1", result);
    }

    // testAddToCart_ProductNotFound tc17
    @Test
    public void testAddToCart_ProductNotFoundtc17() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        when(productService.getProductDetailByProductIdAndVariationId(1, 0)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn("/product-detail/1");
        String result = cartController.addToCart(1, 0, 999999999, redirectAttributes, request);
        assertEquals("Không thể thêm sản phẩm vào giỏ hàng", redirectAttributes.getFlashAttributes().get("error"));
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
    /// testUpdateCartQuantity_SuccessfulUpdate tc1
    @Test
    public void testUpdateCartQuantity_SuccessfulUpdatetc1() {
        Integer cartItemId = 1;
        Integer quantity = 2;
        CartItem mockCartItem = new CartItem();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(5);
        mockCartItem.setVariationId(productVariation);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        assertEquals("redirect:/cart-items", result);
        verify(cartItemService, times(1)).updateCartItemQuantity(cartItemId, quantity);
        assertEquals("Cập nhật giỏ hàng thành công", redirectAttributes.getFlashAttributes().get("message"));
    }

    // testUpdateCartQuantity_ExceedsStock tc2
    @Test
    public void testUpdateCartQuantity_ExceedsStocktc2() {
        Integer cartItemId = 1;
        Integer quantity = 0;
        CartItem mockCartItem = new CartItem();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(5);
        mockCartItem.setVariationId(productVariation);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        assertEquals("redirect:/cart-items", result);
        verify(cartItemService, never()).updateCartItemQuantity(anyInt(), anyInt());
        assertEquals("Error updating quantity: Exceeds stock limit", redirectAttributes.getFlashAttributes().get("error"));
    }

    // testUpdateCartQuantity_ExceedsStock tc3
    @Test
    public void testUpdateCartQuantity_ExceedsStocktc3() {
        Integer cartItemId = 1;
        Integer quantity = -1;
        CartItem mockCartItem = new CartItem();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        mockCartItem.setVariationId(productVariation);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        assertEquals("redirect:/cart-items", result);
        verify(cartItemService, never()).updateCartItemQuantity(anyInt(), anyInt());
        assertEquals("Error updating quantity: Exceeds stock limit", redirectAttributes.getFlashAttributes().get("error"));
    }

    // testUpdateCartQuantity_ExceedsStock tc4
    @Test
    public void testUpdateCartQuantity_ExceedsStocktc4() {
        Integer cartItemId = 1;
        Integer quantity = 999999999;
        CartItem mockCartItem = new CartItem();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(5);
        mockCartItem.setVariationId(productVariation);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        assertEquals("redirect:/cart-items", result);
        verify(cartItemService, never()).updateCartItemQuantity(anyInt(), anyInt());
        assertEquals("Lỗi khi cập nhật số lượng: Vượt quá số lượng tồn kho", redirectAttributes.getFlashAttributes().get("error"));
    }

    // testUpdateCartQuantity_ExceedsStock tc5
    @Test
    public void testUpdateCartQuantity_ExceedsStocktc5() {
        Integer cartItemId = 1;
        Integer quantity = null;
        CartItem mockCartItem = new CartItem();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(5);
        mockCartItem.setVariationId(productVariation);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(mockCartItem));
        String result = cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        assertEquals("redirect:/cart-items", result);
        verify(cartItemService, never()).updateCartItemQuantity(anyInt(), anyInt());
        assertEquals("Đã xảy ra lỗi khi cập nhật số lượng", redirectAttributes.getFlashAttributes().get("error"));
    }

    // testUpdateCartQuantity_CartItemNotFound tc6
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc6() {
        Integer cartItemId = 0;
        Integer quantity = 2;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc7
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc7() {
        Integer cartItemId = 0;
        Integer quantity = 0;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc8
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc8() {
        Integer cartItemId = 0;
        Integer quantity = -1;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc9
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc9() {
        Integer cartItemId = 0;
        Integer quantity = 999999999;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc10
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc10() {
        Integer cartItemId = 0;
        Integer quantity = null;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc11
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc11() {
        Integer cartItemId = -1;
        Integer quantity = 2;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc12
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc12() {
        Integer cartItemId = -1;
        Integer quantity = 0;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc13
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc13() {
        Integer cartItemId = -1;
        Integer quantity = -1;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc14
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc14() {
        Integer cartItemId = -1;
        Integer quantity = 999999999;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }

    // testUpdateCartQuantity_CartItemNotFound tc15
    @Test
    public void testUpdateCartQuantity_CartItemNotFoundtc15() {
        Integer cartItemId = -1;
        Integer quantity = null;
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(cartItemId, quantity, redirectAttributes);
        });
        assertEquals("Cart item not found", exception.getMessage());
    }










}