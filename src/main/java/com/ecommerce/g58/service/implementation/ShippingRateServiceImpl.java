package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ShippingRate;
import com.ecommerce.g58.repository.ShippingRateRepository;
import com.ecommerce.g58.service.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingRateServiceImpl implements ShippingRateService {
    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Override
    public List<ShippingRate> getAllShippingRates() {
        return shippingRateRepository.findAll();
    }

    @Override
    public ShippingRate getShippingRateById(int rateId) {
        return shippingRateRepository.findById(rateId).orElse(null);
    }

    @Override
    public void saveShippingRate(ShippingRate shippingRate) {
        shippingRateRepository.save(shippingRate);
    }

    @Override
    public void deleteShippingRate(int rateId) {
        shippingRateRepository.deleteById(rateId);
    }
}
