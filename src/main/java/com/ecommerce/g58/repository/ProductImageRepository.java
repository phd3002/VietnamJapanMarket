package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    void deleteByImageId(Integer imageId); // Delete image by imageId

    // Query to fetch images by joining product_variation and product_image using image_id
    @Query("SELECT pi FROM ProductImage pi JOIN ProductVariation pv ON pi.imageId = pv.imageId.imageId WHERE pv.productId.productId = :productId")
    List<ProductImage> findByProductProductId(@Param("productId") Integer productId);

    ProductImage findTopByOrderByImageIdDesc();
}
