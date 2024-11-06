package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ShippingStatus;
import com.ecommerce.g58.repository.ShippingStatusRepository;
import com.ecommerce.g58.service.ShippingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingStatusServiceImp implements ShippingStatusService {

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;

    public ShippingStatusServiceImp(ShippingStatusRepository shippingStatusRepository) {
        this.shippingStatusRepository = shippingStatusRepository;
    }

    @Override
    public ShippingStatus getShippingStatusByOrderId(Integer orderId) {
        return shippingStatusRepository.findByOrderId(orderId);
    }

    @Override
    public void updateShippingStatus(Integer orderId, ShippingStatus shippingStatus) {
        // Tìm đối tượng ShippingStatus dựa trên orderId
        ShippingStatus existingStatus = shippingStatusRepository.findByOrderId(orderId);
        if (existingStatus != null) {
            // Cập nhật trạng thái cho đơn hàng hiện có
            existingStatus.setStatus(shippingStatus.getStatus());
            existingStatus.setTrackingNumber(shippingStatus.getTrackingNumber());
            existingStatus.setUpdatedAt(shippingStatus.getUpdatedAt());
            shippingStatusRepository.save(existingStatus);
        }
    }
}
