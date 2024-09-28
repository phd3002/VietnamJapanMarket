package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Product;
import com.ecommerce.g58.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Fetch all products
    }
    public List<Product> getLatest5Products() {
        return productRepository.findTop5ByOrderByCreatedAtDesc(); // Fetch latest 5 products
    }
}