package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.ShippingUnit;
import com.ecommerce.g58.repository.ShippingUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShippingUnitService {
    List<ShippingUnit> getAllShippingUnits();

    long getTotalShippingUnits();
}
