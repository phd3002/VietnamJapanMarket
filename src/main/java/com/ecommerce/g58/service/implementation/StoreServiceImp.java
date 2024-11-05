package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.Role;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class StoreServiceImp implements StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void registerStore(Stores store) {
        store.setStoreRevenue(0); // Set store revenue to 0 when registering
        storeRepository.save(store);
    }

    @Override
    public Optional<Stores> findByOwnerId(Users ownerId) {
        return storeRepository.findByOwnerId(ownerId);
    }

    @Override
    public Optional<Stores> findByStoreName(String storeName) {
        return storeRepository.findByStoreName(storeName);
    }

    @Override
    @Transactional
    public void updateUserRoleToSeller(Users user) {
        Roles sellerRole = roleRepository.findByRoleName(Role.SELLER.getName()).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoleId(sellerRole);
        userRepository.save(user);
    }

    // Method to fetch store by storeId
    public Stores findStoreById(Integer storeId) {
        return storeRepository.findById(storeId).orElse(null);
    }

    // Method to save store information
    public void saveStore(Stores store) {
        storeRepository.save(store);
    }

    public Optional<Stores> findById(Integer storeId) {
        return storeRepository.findById(storeId);
    }

    @Override
    public long getTotalStores() {
        return storeRepository.count();
    }
}
