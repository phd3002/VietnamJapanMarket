package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface StoresRepository extends JpaRepository<Stores, Integer> {
    Stores findByStoreName(String storeName);
    Page<Stores> findByStoreNameContainingIgnoreCase(String storeName, Pageable pageable);
}
