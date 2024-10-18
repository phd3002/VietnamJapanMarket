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

    // Modify (update) shipping address
    @PutMapping("/modify/{orderId}")
    public ResponseEntity<Map<String, String>> modifyShippingAddress(@PathVariable Integer orderId, @RequestBody String newAddress) {
        shippingAddressService.updateShippingAddress(orderId, newAddress);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Shipping address updated successfully");
        return ResponseEntity.ok(response);
    }

    // View saved shipping address
    @GetMapping("/view/{orderId}")
    public ResponseEntity<String> viewShippingAddress(@PathVariable Integer orderId) {
        String address = shippingAddressService.getShippingAddress(orderId);
        return ResponseEntity.ok(address);
    }

    // Optional: Remove saved shipping address (set it to null)
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, String>> deleteShippingAddress(@PathVariable Integer orderId) {
        shippingAddressService.deleteShippingAddress(orderId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Shipping address removed");
        return ResponseEntity.ok(response);
    }
}