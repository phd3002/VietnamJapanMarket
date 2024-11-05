package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ShippingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingUnitRepository extends JpaRepository<ShippingUnit, Integer> {
    long count();
}
