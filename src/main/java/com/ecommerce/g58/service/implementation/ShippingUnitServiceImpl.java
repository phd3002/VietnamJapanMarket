package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ShippingUnit;
import com.ecommerce.g58.repository.ShippingUnitRepository;
import com.ecommerce.g58.service.ShippingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingUnitServiceImpl implements ShippingUnitService {

    @Autowired
    private ShippingUnitRepository shippingUnitRepository;

    @Override
    public List<ShippingUnit> getAllShippingUnits() {
        return shippingUnitRepository.findAll();
    }
}
