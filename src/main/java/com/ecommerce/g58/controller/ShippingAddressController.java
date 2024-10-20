package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping-address")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    // thay đổi địa chỉ giao hàng
    @PutMapping("/modify/{orderId}")
    public ResponseEntity<Map<String, String>> modifyShippingAddress(@PathVariable Integer orderId, @RequestBody String newAddress) {
        shippingAddressService.updateShippingAddress(orderId, newAddress);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã cập nhật địa chỉ giao hàng mới");
        return ResponseEntity.ok(response);
    }
}