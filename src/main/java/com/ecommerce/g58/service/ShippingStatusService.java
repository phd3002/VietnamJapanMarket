package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.ShippingStatus;

public interface ShippingStatusService {
    ShippingStatus getShippingStatusByOrderId(Integer orderId);

    void updateShippingStatus(Integer orderId, ShippingStatus shippingStatus);
}
