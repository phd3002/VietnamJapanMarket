package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findAll();
    List<Products> findTop5ByOrderByCreatedAtDesc();
//    @Query("select distinct pv.productId, pi.thumbnail, p.productName, p.price from Products p, ProductImage pi, ProductVariation pv where pi.imageId = :imageId and pv.productId = :productId")
//    List<ProductDTO> findProductDetails(@Param("imageId") Integer imageId, @Param("productId") Integer productId);
@Query(value = "SELECT DISTINCT p.product_id AS productId, pi.thumbnail AS thumbnail, p.product_name AS productName, p.price AS price " +
        "FROM products p " +
        "JOIN product_variation pv ON p.product_id = pv.product_id " +
        "JOIN product_image pi ON pv.image_id = pi.image_id " +
        "WHERE p.price IS NOT NULL AND pi.thumbnail IS NOT NULL", nativeQuery = true)
List<Object[]> findProductDetailsNative();


}
