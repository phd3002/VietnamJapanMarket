package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    // Query to fetch images by joining product_variation and product_image using image_id
    @Query("SELECT pi FROM ProductImage pi JOIN ProductVariation pv ON pi.imageId = pv.productImage.imageId WHERE pv.productId.productId = :productId")
    List<ProductImage> findByProductProductId(@Param("productId") Integer productId);
}
