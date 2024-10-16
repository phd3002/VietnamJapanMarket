package com.ecommerce.g58.controller;

import com.ecommerce.g58.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping-address")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    // Modify (update) shipping address
    @PutMapping("/modify/{orderId}")
    public ResponseEntity<String> modifyShippingAddress(@PathVariable Integer orderId, @RequestBody String newAddress) {
        shippingAddressService.updateShippingAddress(orderId, newAddress);
        return ResponseEntity.ok("Shipping address updated successfully");
    }

    // View saved shipping address
    @GetMapping("/view/{orderId}")
    public ResponseEntity<String> viewShippingAddress(@PathVariable Integer orderId) {
        String address = shippingAddressService.getShippingAddress(orderId);
        return ResponseEntity.ok(address);
    }

    // Optional: Remove saved shipping address (set it to null)
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteShippingAddress(@PathVariable Integer orderId) {
        shippingAddressService.deleteShippingAddress(orderId);
        return ResponseEntity.ok("Shipping address removed");
    }
}

