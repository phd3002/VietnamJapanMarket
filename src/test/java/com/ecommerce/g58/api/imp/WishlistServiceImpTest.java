//package com.ecommerce.g58.api.imp;
//
//import com.ecommerce.g58.dto.ProductDetailDTO;
//import com.ecommerce.g58.entity.ProductVariation;
//import com.ecommerce.g58.entity.Products;
//import com.ecommerce.g58.entity.Users;
//import com.ecommerce.g58.entity.Wishlist;
//import com.ecommerce.g58.repository.ProductVariationRepository;
//import com.ecommerce.g58.repository.UserRepository;
//import com.ecommerce.g58.repository.WishlistRepository;
//import com.ecommerce.g58.service.implementation.WishlistServiceImp;
//import lombok.var;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class WishlistServiceImpTest {
//
//    @Autowired
//    private WishlistServiceImp wishlistService;
//
//    @MockBean
//    private WishlistRepository wishlistRepository;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private Products products;
//
//    @MockBean
//    private ProductVariationRepository productVariationRepository;
//
//    private Users user;
//    private ProductDetailDTO productDetailDTO;
//    private ProductVariation productVariation;
//    private Wishlist wishlist;
//
//    @BeforeEach
//    public void setUp() {
//        user = new Users();
//        user.setUserId(1);
//        user.setEmail("test@example.com");
//
//        productDetailDTO = new ProductDetailDTO();
//        productDetailDTO.setVariationId(1);
//
//        productVariation = new ProductVariation();
//        productVariation.setVariationId(1);
//        productVariation.setProductId(products);
//
//        wishlist = Wishlist.builder()
//                .userId(user)
//                .product(productVariation.getProductId())
//                .productVariation(productVariation)
//                .addedAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    public void testAddProductToWishlist_Success() {
//        when(productVariationRepository.findById(anyInt())).thenReturn(Optional.of(productVariation));
//        when(wishlistRepository.findByUserAndProductVariation(anyInt(), any(ProductVariation.class))).thenReturn(Optional.empty());
//
//        wishlistService.addProductToWishlist(user, productDetailDTO);
//
//        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
//    }
//
//    @Test
//    public void testAddProductToWishlist_AlreadyExists() {
//        when(productVariationRepository.findById(anyInt())).thenReturn(Optional.of(productVariation));
//        when(wishlistRepository.findByUserAndProductVariation(anyInt(), any(ProductVariation.class))).thenReturn(Optional.of(wishlist));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            wishlistService.addProductToWishlist(user, productDetailDTO);
//        });
//
//        assertEquals("Product is already in the wishlist", exception.getMessage());
//        verify(wishlistRepository, never()).save(any(Wishlist.class));
//    }
//
//    @Test
//    public void testGetUserWishlist() {
//        when(wishlistRepository.findByUser(anyInt())).thenReturn(Collections.singletonList(wishlist));
//
//        var wishlistItems = wishlistService.getUserWishlist(1);
//
//        assertNotNull(wishlistItems);
//        assertEquals(1, wishlistItems.size());
//        assertEquals(1, wishlistItems.get(0).getProductVariation().getVariationId());
//    }
//}