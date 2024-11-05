package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.ShippingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ShippingStatusService shippingStatusService;
}
