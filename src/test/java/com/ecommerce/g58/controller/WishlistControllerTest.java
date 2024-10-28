//package com.ecommerce.g58.controller;
//
//import com.ecommerce.g58.entity.Users;
//import com.ecommerce.g58.entity.Wishlist;
//import com.ecommerce.g58.service.UserService;
//import com.ecommerce.g58.service.WishlistService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.ui.Model;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class WishlistControllerTest {
//
//    @InjectMocks
//    private WishlistController wishlistController;  // Inject WishlistController
//
//    @Mock
//    private UserService userService;  // Mock UserService
//
//    @Mock
//    private WishlistService wishlistService;  // Mock WishlistService
//
//    @Mock
//    private Model model;  // Mock Model
//
//    @Mock
//    private Authentication authentication;  // Mock Authentication
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);  // Initialize mocks
//    }
//
//    // Test case for when the user is authenticated and the wishlist is not empty
//    @Test
//    public void testGetWishlist_UserAuthenticated_WishlistNotEmpty() {
//        // Arrange
//        // Mock authentication details for a logged-in user
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(authentication.getName()).thenReturn("testUser");
//
//        // Mock user details
//        Users user = new Users();
//        user.setUserId(1);
//        when(userService.findByEmail("testUser")).thenReturn(user);
//
//        // Mock wishlist items
//        Wishlist wishlist1 = new Wishlist();
//        Wishlist wishlist2 = new Wishlist();
//        List<Wishlist> wishlistItems = Arrays.asList(wishlist1, wishlist2);
//        when(wishlistService.getUserWishlist(1)).thenReturn(wishlistItems);
//
//        // Act
//        String viewName = wishlistController.getWishlist(model);
//
//        // Assert
//        assertEquals("redirect:/sign-in", viewName);  // Expecting to return "wishlist" view
//        verify(model, times(1)).addAttribute("wishlistItems", wishlistItems);  // Verifying that the wishlist items are added to the model
//    }
//
//    // Test case for when the user is authenticated but the wishlist is empty
//    @Test
//    public void testGetWishlist_UserAuthenticated_WishlistEmpty() {
//        // Arrange
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(authentication.getName()).thenReturn("testUser");
//
//        Users user = new Users();
//        user.setUserId(1);
//        when(userService.findByEmail("testUser")).thenReturn(user);
//
//        when(wishlistService.getUserWishlist(1)).thenReturn(Arrays.asList());
//
//        // Act
//        String viewName = wishlistController.getWishlist(model);
//
//        // Assert
//        assertEquals("redirect:/sign-in", viewName);  // Expecting to return "wishlist" view
//        verify(model, times(1)).addAttribute("message", "Your wishlist is empty.");
//    }
//
//    // Test case for when the user is not authenticated (anonymous user)
//    @Test
//    public void testGetWishlist_UserNotAuthenticated() {
//        // Arrange
//        when(authentication.isAuthenticated()).thenReturn(false);
//
//        // Act
//        String viewName = wishlistController.getWishlist(model);
//
//        // Assert
//        assertEquals("redirect:/sign-in", viewName);  // Expecting to redirect to sign-in page
//        verify(model, never()).addAttribute(anyString(), any());  // Ensure no model attributes are added
//    }
//}
