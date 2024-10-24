package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface StoreService {
    void registerStore(Stores store);
    Optional<Stores> findByOwnerId(Users ownerId);
    Optional<Stores> findByStoreName(String storeName);
    void updateUserRoleToSeller(Users user);
}
