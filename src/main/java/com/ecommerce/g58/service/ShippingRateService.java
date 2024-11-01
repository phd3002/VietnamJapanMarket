package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.ShippingRate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShippingRateService {
    List<ShippingRate> getAllShippingRates();
    ShippingRate getShippingRateById(int rateId);
    void saveShippingRate(ShippingRate shippingRate);
    void deleteShippingRate(int rateId);
}
