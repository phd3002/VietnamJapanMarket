package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.BestSellingDTO;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StoreService {
    void registerStore(Stores store);

    Optional<Stores> findByOwnerId(Users ownerId);

    Optional<Stores> findByStoreName(String storeName);

    void updateUserRoleToSeller(Users user);

    Stores findStoreById(Integer storeId);

    void saveStore(Stores store);

    Optional<Stores> findById(Integer storeId);

    long getTotalStores();

    Integer calculateTotalRevenue(Integer userId, String startDate, String endDate);

    Integer calculateTotalProducts(Integer userId);

    Integer totalOrders(Integer userId, String startDate, String endDate);

    Integer totalOrdersCompleted(Integer userId, String startDate, String endDate);

    Integer totalOrdersCancelledAndReturned(Integer userId, String startDate, String endDate);

    List<BestSellingDTO> getBestSellingProducts(Integer userId, String startDate, String endDate);
}
