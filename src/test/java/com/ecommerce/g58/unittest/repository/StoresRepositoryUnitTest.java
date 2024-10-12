//package com.ecommerce.g58.unittest.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//import com.ecommerce.g58.entity.Stores;
//import com.ecommerce.g58.repository.StoresRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//public class StoresRepositoryUnitTest {
//    @Mock
//    private StoresRepository storesRepository;
//
//    @InjectMocks
//    private StoreService storeService; // Giả sử bạn có một service gọi là StoreService để xử lý logic
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testFindByStoreNameContainingIgnoreCase() {
//        // Given
//        String storeName = "test";
//        Stores store1 = new Stores();
//        store1.setStoreName("Test Store 1");
//
//        Stores store2 = new Stores();
//        store2.setStoreName("Test Store 2");
//
//        List<Stores> storeList = Arrays.asList(store1, store2);
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Stores> storePage = new PageImpl<>(storeList, pageable, storeList.size());
//
//        // Mocking behavior
//        when(storesRepository.findByStoreNameContainingIgnoreCase(storeName, pageable)).thenReturn(storePage);
//
//        // When
//        Page<Stores> foundStores = storeService.findByStoreNameContainingIgnoreCase(storeName, pageable);
//
//        // Then
//        assertThat(foundStores.getContent()).hasSize(2);
//        assertThat(foundStores.getContent().get(0).getStoreName()).isEqualTo("Test Store 1");
//        assertThat(foundStores.getContent().get(1).getStoreName()).isEqualTo("Test Store 2");
//    }
//
//    @Test
//    void testFindByStoreNameNotFound() {
//        // Given
//        String storeName = "nonexistent";
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Stores> storePage = new PageImpl<>(Arrays.asList());
//
//        // Mocking behavior
//        when(storesRepository.findByStoreNameContainingIgnoreCase(storeName, pageable)).thenReturn(storePage);
//
//        // When
//        Page<Stores> foundStores = storeService.findByStoreNameContainingIgnoreCase(storeName, pageable);
//
//        // Then
//        assertThat(foundStores.getContent()).isEmpty();
//    }
//
//    @Test
//    void testFindByStoreNameWithNull() {
//        // When
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Stores> foundStores = storeService.findByStoreNameContainingIgnoreCase(null, pageable);
//
//        // Then
//        assertThat(foundStores.getContent()).isEmpty(); // Không tìm thấy cửa hàng nào
//    }
//
//
//}
