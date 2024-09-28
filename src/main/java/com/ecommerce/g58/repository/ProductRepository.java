package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findTop5ByOrderByCreatedAtDesc();
}
