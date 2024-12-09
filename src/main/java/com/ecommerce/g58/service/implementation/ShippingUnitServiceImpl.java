package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ShippingUnit;
import com.ecommerce.g58.repository.OrderRepository;
import com.ecommerce.g58.repository.ShippingUnitRepository;
import com.ecommerce.g58.service.ShippingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingUnitServiceImpl implements ShippingUnitService {

    @Autowired
    private OrderRepository orderRepository;

//    @Autowired
//    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private ShippingUnitRepository shippingUnitRepository;

    @Override
    public List<ShippingUnit> getAllShippingUnits() {
        return shippingUnitRepository.findAll();
    }

    @Override
    public long getTotalShippingUnits() {
        return shippingUnitRepository.count();
    }

    @Override
    public void addShippingUnit(ShippingUnit shippingUnit) {
        shippingUnitRepository.save(shippingUnit);
    }

    @Override
    public void deleteShippingUnit(int id) {
        if (shippingUnitRepository.existsById(id)) {
            orderRepository.unsetShippingUnitInOrders(id);
//            shippingRateRepository.unsetShippingUnitInShippingRates(id);
            shippingUnitRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(int id) {
        return shippingUnitRepository.existsById(id);
    }

    @Override
    public Optional<ShippingUnit> findById(int id) {
        return shippingUnitRepository.findById(id);
    }
}
