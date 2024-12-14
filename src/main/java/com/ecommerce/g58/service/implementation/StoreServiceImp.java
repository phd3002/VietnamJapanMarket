package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.BestSellingDTO;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.Role;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public Integer calculateTotalRevenue(Integer storeUserId, String startDate, String endDate) {
        return storeRepository.calculateTotalRevenue(storeUserId, startDate, endDate);
    }

    @Override
    public Integer totalProductsSold(Integer userId, String startDate, String endDate) {
        return storeRepository.totalProductSold(userId, startDate, endDate);
    }

    @Override
    public Integer totalOrders(Integer userId, String startDate, String endDate) {
        return storeRepository.totalOrder(userId, startDate, endDate);
    }

    @Override
    public Integer totalOrdersCompleted(Integer userId, String startDate, String endDate) {
        return storeRepository.totalOrderComplete(userId, startDate, endDate);
    }

    @Override
    public Integer totalOrdersCancelledAndReturned(Integer userId, String startDate, String endDate) {
        return storeRepository.totalOrderCancelledAndReturned(userId, startDate, endDate);
    }

    @Override
    public List<BestSellingDTO> getBestSellingProducts(Integer userId, String startDate, String endDate) {
        List<Object[]> result = storeRepository.bestSelling(userId, startDate, endDate);
        List<BestSellingDTO> bestSellingDTOS = new ArrayList<>();
        for (Object[] objects : result) {
            BestSellingDTO dto = new BestSellingDTO();
            dto.setProductCode((String) objects[0]);
            dto.setProductName((String) objects[1]);
            dto.setPrice((Integer) objects[2]);
            dto.setCategory((String) objects[3]);
            if (objects[4] instanceof BigDecimal) {
                dto.setQuantitySold(((BigDecimal) objects[4]).intValue());
            } else if (objects[4] instanceof Integer) {
                dto.setQuantitySold((Integer) objects[4]);
            }
            bestSellingDTOS.add(dto);
        }
        return bestSellingDTOS;
    }

    @Override
    public Integer count5StarFeedback(Integer userId, String startDate, String endDate) {
        return storeRepository.countRatings5Star(userId, startDate, endDate);
    }

    @Override
    public Integer count4StarFeedback(Integer userId, String startDate, String endDate) {
        return storeRepository.countRatings4Star(userId, startDate, endDate);
    }

    @Override
    public Integer count3StarFeedback(Integer userId, String startDate, String endDate) {
        return storeRepository.countRatings3Star(userId, startDate, endDate);
    }

    @Override
    public Integer count2StarFeedback(Integer userId, String startDate, String endDate) {
        return storeRepository.countRatings2Star(userId, startDate, endDate);
    }

    @Override
    public Integer count1StarFeedback(Integer userId, String startDate, String endDate) {
        return storeRepository.countRatings1Star(userId, startDate, endDate);
    }

    @Override
    public Integer totalRevenueCurrentMonth(Integer userId) {
        return storeRepository.totalRevenueCurrent(userId);
    }

    @Override
    public Integer totalRevenueLastMonth(Integer userId) {
        return storeRepository.totalRevenueLastMonth(userId);
    }

    @Override
    public Integer totalRevenueLast2Months(Integer userId) {
        return storeRepository.totalRevenueLast2Months(userId);
    }

    @Override
    public Integer totalRevenueLast3Months(Integer userId) {
        return storeRepository.totalRevenueLast3Months(userId);
    }

    @Override
    public Integer totalRevenueLast4Months(Integer userId) {
        return storeRepository.totalRevenueLast4Months(userId);
    }

    @Override
    public Integer totalRevenueLast5Months(Integer userId) {
        return storeRepository.totalRevenueLast5Months(userId);
    }


}
