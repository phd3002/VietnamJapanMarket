package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Integer> {
    List<ProductVariation> findByProductIdProductId(Integer productId); // Fetch variations by productId
}
