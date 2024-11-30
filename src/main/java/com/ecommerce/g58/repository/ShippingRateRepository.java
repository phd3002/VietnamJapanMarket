package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE shipping_rate sr SET sr.unit_id = NULL WHERE sr.unit_id = :unitId ", nativeQuery = true)
    void unsetShippingUnitInShippingRates(@Param("unitId") int unitId);
}

