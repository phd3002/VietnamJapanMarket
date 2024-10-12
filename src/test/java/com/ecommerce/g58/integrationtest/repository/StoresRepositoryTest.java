package com.ecommerce.g58.integrationtest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.repository.StoresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class StoresRepositoryTest {

    @Autowired
    private StoresRepository storesRepository;

    @BeforeEach
    void setUp() {
        Stores store1 = new Stores();
        store1.setStoreName("Quyet Store");
        storesRepository.save(store1);

        Stores store2 = new Stores();
        store2.setStoreName("Hưng Store");
        storesRepository.save(store2);
    }

    // Test case for findByStoreName
    @Test
    void testFindByStoreName() {
        // When
        Stores foundStore1 = storesRepository.findByStoreName("Quyet Store");
        Stores foundStore2 = storesRepository.findByStoreName("Hưng Store");

        // Then
        assertThat(foundStore1).isNotNull();
        assertThat(foundStore1.getStoreName()).isEqualTo("Quyet Store");
        assertThat(foundStore2).isNotNull();
        assertThat(foundStore2.getStoreName()).isEqualTo("Hưng Store");
    }

    // Test case for findByStoreName when store not found
    @Test
    void testFindByStoreNameNotFound() {
        // When
        Stores foundStore = storesRepository.findByStoreName("Khoi Store ");

        // Then
        assertThat(foundStore).isNull();
    }

    // Test case for findByStoreNameContainingIgnoreCase
    @Test
    void testFindByStoreCasNameContainingIgnoree() {
        // Given
        String storeName = "Store";

        // When
        Pageable pageable = PageRequest.of(0, 5);
        Page<Stores> foundStores = storesRepository.findByStoreNameContainingIgnoreCase(storeName, pageable);

        // Then
        assertThat(foundStores.getContent()).hasSize(2); // Chúng ta đã lưu 2 cửa hàng
        assertThat(foundStores.getContent().get(0).getStoreName()).isEqualTo("Quyet Store");
        assertThat(foundStores.getContent().get(1).getStoreName()).isEqualTo("Hưng Store");
    }

    // Test case for findByStoreNameContainingIgnoreCase when no store matches
    @Test
    void testFindByStoreNameContainingIgnoreCaseNotFound() {
        // Given
        String storeName = "Nonexistent";

        // When
        Pageable pageable = PageRequest.of(0, 5);
        Page<Stores> foundStores = storesRepository.findByStoreNameContainingIgnoreCase(storeName, pageable);

        // Then
        assertThat(foundStores.getContent()).isEmpty();
    }
}
