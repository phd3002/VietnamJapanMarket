package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Products, Integer> {
    List<Products> findAll();
    List<Products> findTop5ByOrderByCreatedAtDesc();
    List<Products> findByProductNameContainingIgnoreCase(String productName);
    @Query(value = "SELECT DISTINCT p.product_id AS productId, " +
            "pi.thumbnail AS thumbnail, " +
            "p.product_name AS productName, " +
            "p.price AS price, " +
            "pv.variation_id AS variantId, " + // Include variant ID
            "pi.image_id AS imageId " + // Include image ID for cart
            "FROM products p " +
            "JOIN product_variation pv ON p.product_id = pv.product_id " +
            "JOIN product_image pi ON pv.image_id = pi.image_id " +
            "WHERE p.price IS NOT NULL AND pi.thumbnail IS NOT NULL", nativeQuery = true)
    List<Object[]> findProductDetailsNative();
    @Query(value = "SELECT * FROM products p WHERE p.category_id = :categoryId", nativeQuery = true)
    List<Products> findByCategoryId(@Param("categoryId") Long categoryId);
}
