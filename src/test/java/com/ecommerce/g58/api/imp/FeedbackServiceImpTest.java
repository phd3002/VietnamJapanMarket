//package com.ecommerce.g58.api.imp;
//
//import com.ecommerce.g58.entity.Feedback;
//import com.ecommerce.g58.entity.ProductVariation;
//import com.ecommerce.g58.entity.Stores;
//import com.ecommerce.g58.entity.Users;
//import com.ecommerce.g58.enums.FeedbackStatus;
//import com.ecommerce.g58.repository.FeedbackRepository;
//import com.ecommerce.g58.service.implementation.FeedbackServiceImp;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class FeedbackServiceImpTest {
//
//    @Autowired
//    private FeedbackServiceImp feedbackService;
//
//    @MockBean
//    private FeedbackRepository feedbackRepository;
//
//    private Users user;
//    private ProductVariation productVariation;
//    private Stores store;
//    private Feedback feedback;
//
//    @BeforeEach
//    public void setUp() {
//        user = new Users();
//        user.setUserId(1);
//        user.setEmail("test@example.com");
//
//        productVariation = new ProductVariation();
//        productVariation.setVariationId(1);
//
//        store = new Stores();
//        store.setStoreId(1);
//
//        feedback = Feedback.builder()
//                .userId(user)
//                .comment("Great service!")
//                .createdAt(LocalDateTime.now())
//                .variationId(productVariation)
//                .storeId(store)
//                .feedbackStatus(FeedbackStatus.PENDING)
//                .build();
//    }
//
//    @Test
//    public void testCreateFeedback() {
//        when(feedbackRepository.save(any(Feedback.class))).thenReturn(feedback);
//
//        Feedback createdFeedback = feedbackService.createFeedback(user, "Great service!", productVariation, store);
//
//        assertNotNull(createdFeedback);
//        assertEquals("Great service!", createdFeedback.getComment());
//        verify(feedbackRepository, times(1)).save(any(Feedback.class));
//    }
//
//    @Test
//    public void testGetFeedbacksByCanteenIdAndStatus() {
//        when(feedbackRepository.findFeedbackByCanteenIdAndStatus(anyInt(), any(FeedbackStatus.class)))
//                .thenReturn(Collections.singletonList(feedback));
//
//        List<Feedback> feedbacks = feedbackService.getFeedbacksByCanteenIdAndStatus(1, FeedbackStatus.PENDING);
//
//        assertNotNull(feedbacks);
//        assertEquals(1, feedbacks.size());
//        assertEquals(FeedbackStatus.PENDING, feedbacks.get(0).getFeedbackStatus());
//    }
//
//
//
//    @Test
//    public void testFindByStatusAndDateRange() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Feedback> page = new PageImpl<>(Collections.singletonList(feedback));
//        when(feedbackRepository.findByStatusAndDateRange(any(FeedbackStatus.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
//                .thenReturn(page);
//
//        Page<Feedback> feedbackPage = feedbackService.findByStatusAndDateRange(FeedbackStatus.PENDING, LocalDateTime.now().minusDays(1), LocalDateTime.now(), pageable);
//
//        assertNotNull(feedbackPage);
//        assertEquals(1, feedbackPage.getTotalElements());
//    }
//}