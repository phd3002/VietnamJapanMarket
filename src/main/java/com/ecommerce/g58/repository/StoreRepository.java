package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Stores, Integer> {
    Optional<Stores> findByStoreName(String storeName);
    Page<Stores> findByStoreNameContainingIgnoreCase(String storeName, Pageable pageable);
    Optional<Stores> findByOwnerId(Users ownerId);
}
