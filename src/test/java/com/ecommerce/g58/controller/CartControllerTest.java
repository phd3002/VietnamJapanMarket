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

    // testAddToCarttc1
    @Test
    public void testAddToCart_tc1() {
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

    // testAddToCart tc2
    @Test
    public void testAddToCart_tc2() {
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

    // testAddToCart tc3
    @Test
    public void testAddToCart_tc3() {
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

    // testAddToCart tc4
    @Test
    public void testAddToCart_tc4() {
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

    // testAddToCart tc5
    @Test
    public void testAddToCart_tc5() {
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

    // testAddToCart tc6
    @Test
    public void testAddToCart_tc6() {
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

    // testAddToCart tc7
    @Test
    public void testAddToCart_tc7() {
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

    // testAddToCart tc8
    @Test
    public void testAddToCart_tc8() {
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

    // testAddToCart tc9
    @Test
    public void testAddToCart_tc9() {
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

    // testAddToCart tc10
    @Test
    public void testAddToCart_tc10() {
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

    // testAddToCart tc11
    @Test
    public void testAddToCart_tc11() {
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

    // testAddToCart tc12
    @Test
    public void testAddToCart_tc12() {
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
    // testAddToCart tc13
    @Test
    public void testAddToCart_tc13() {
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

    // testAddToCart_ tc14
    @Test
    public void testAddToCart_tc14() {
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

    // testAddToCart tc15
    @Test
    public void testAddToCart_tc15() {
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

    // testAddToCart tc16
    @Test
    public void testAddToCart_tc16() {
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

    // testAddToCart tc17
    @Test
    public void testAddToCart_tc17() {
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

    @Test
    public void testAddToCart_tc18() {
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Users user = new Users();
        user.setUserId(1);
        user.setEmail("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        ProductDetailDTO productDetail = new ProductDetailDTO();
        productDetail.setStoreId(2);
        when(productService.getProductDetailByProductIdAndVariationId(1, 2)).thenReturn(productDetail);
        CartItem existingCartItem = new CartItem();
        Products existingProduct = new Products();
        Stores existingStore = new Stores();
        existingStore.setStoreId(1);
        existingProduct.setStoreId(existingStore);
        existingCartItem.setProductId(existingProduct);
        List<CartItem> cartItems = Collections.singletonList(existingCartItem);
        when(cartItemService.getCartItemsByUserId(1)).thenReturn(cartItems);
        when(request.getHeader("Referer")).thenReturn("/product-detail");
        String result = cartController.addToCart(1, 2, 3, mockRedirectAttributes, request);
        assertEquals("redirect:/product-detail", result);
        verify(mockRedirectAttributes, times(1)).addFlashAttribute("error",
                "Bạn không thể thêm sản phẩm từ cửa hàng khác vào giỏ hàng. Vui lòng thanh toán hoặc xóa các sản phẩm hiện tại trong giỏ hàng trước.");
    }

    @Test
    public void testAddToCart_tc19() {
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Users user = new Users();
        user.setUserId(1);
        user.setEmail("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        ProductDetailDTO productDetail = new ProductDetailDTO();
        productDetail.setStoreId(1);
        when(productService.getProductDetailByProductIdAndVariationId(1, 2)).thenReturn(productDetail);
        Cart mockCart = mock(Cart.class);
        when(cartService.getOrCreateCart(user)).thenReturn(mockCart);
        doThrow(new IllegalArgumentException("Test exception")).when(cartService).addProductToCart(user, productDetail, 3, mockCart);
        when(request.getHeader("Referer")).thenReturn("/product-detail");
        String result = cartController.addToCart(1, 2, 3, mockRedirectAttributes, request);
        assertEquals("redirect:/product-detail", result);
        verify(mockRedirectAttributes, times(1)).addFlashAttribute("error", "Đã có lỗi xảy ra khi thêm vào giỏ hảng");
    }

    //--------------------------------------------------------------------------------------------------------------
    /// testUpdateCartQuantity tc1
    @Test
    public void testUpdateCartQuantity_tc1() {
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

    // testUpdateCartQuantity tc2
    @Test
    public void testUpdateCartQuantity_tc2() {
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

    // testUpdateCartQuantity tc3
    @Test
    public void testUpdateCartQuantity_tc3() {
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

    // testUpdateCartQuantity tc4
    @Test
    public void testUpdateCartQuantity_tc4() {
        when(cartItemRepository.findById(1)).thenReturn(Optional.empty()); // Mock no cart item found
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartController.updateCartQuantity(1, 2, redirectAttributes);
        });
        assertEquals("Không tìm thấy mục giỏ hàng", exception.getMessage());
    }





}