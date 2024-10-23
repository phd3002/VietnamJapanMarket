//package com.ecommerce.g58.controller;
//
//import com.ecommerce.g58.config.TestSecurityConfig;
//import com.ecommerce.g58.entity.Feedback;
//import com.ecommerce.g58.entity.ProductVariation;
//import com.ecommerce.g58.entity.Stores;
//import com.ecommerce.g58.entity.Users;
//import com.ecommerce.g58.service.FeedbackService;
//import com.ecommerce.g58.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(FeedbackController.class)
//@ContextConfiguration(classes = TestSecurityConfig.class)
//public class FeedbackControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private FeedbackService feedbackService;
//
//    @MockBean
//    private UserService userService;
//
//    private Users user;
//
//    @BeforeEach
//    public void setUp() {
//        user = new Users();
//        user.setEmail("lequyet180902@gmail.com");
//        // Initialize other necessary fields of the user
//    }
//
//    @Test
//    @WithMockUser(username = "lequyet180902@gmail.com", roles = {"USER"})
//    public void testCreateFeedbackStore() throws Exception {
//        Stores store = new Stores();
//        store.setStoreId(1);
//        ProductVariation productVariation = new ProductVariation();
//        productVariation.setVariationId(1);
//        when(userService.findByEmail(anyString())).thenReturn(user);
//        when(feedbackService.createFeedback(any(Users.class), anyString(), any(ProductVariation.class), any(Stores.class)))
//                .thenReturn(new Feedback());
//
//        mockMvc.perform(post("/create_new_feedback_store")
//                        .param("comment", "Great service!")
//                        .param("storeId", "1")
//                        .param("productVariationId", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/store_details?id=1"));
//    }
//
//    @Test
//    @WithMockUser(username = "lequyet180902@gmail.com", roles = {"USER"})
//    public void testCreateFeedbackProduct() throws Exception {
//        Stores store = new Stores();
//        store.setStoreId(1);
//        ProductVariation productVariation = new ProductVariation();
//        productVariation.setVariationId(1);
//        when(userService.findByEmail(anyString())).thenReturn(user);
//        when(feedbackService.createFeedback(any(Users.class), anyString(), any(ProductVariation.class), any(Stores.class)))
//                .thenReturn(new Feedback());
//
//        mockMvc.perform(post("/create_new_feedback_product")
//                        .param("comment", "Great product!")
//                        .param("storeId", "1")
//                        .param("productVariationId", "1"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/product_details?id=1"));
//    }
//
//    @Test
//    @WithMockUser(username = "lequyet180902@gmail.com", roles = {"USER"})
//    public void testFeedbackSystemForm() throws Exception {
//        when(userService.getUserById(1)).thenReturn(user);
//
//        mockMvc.perform(get("/feedback-system-form/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("feedback-system-form"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("feedback"));
//    }
//
//    @Test
//    @WithMockUser(username = "lequyet180902@gmail.com", roles = {"USER"})
//    public void testSubmitFeedback() throws Exception {
//        when(userService.getUserById(1)).thenReturn(user);
//        when(feedbackService.createFeedback(any(Users.class), anyString(), any(ProductVariation.class), any(Stores.class)))
//                .thenReturn(new Feedback());
//
//        mockMvc.perform(post("/submit-feedback/1")
//                        .param("comment", "Great service!"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/feedback-system-form/1"));
//    }
//}