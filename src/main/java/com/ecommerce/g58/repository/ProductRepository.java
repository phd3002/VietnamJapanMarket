package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Products, Integer> {
    List<Products> findAll();
    List<Products> findTop5ByOrderByCreatedAtDesc();
}
