package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.ProductVariationDTO;
import com.ecommerce.g58.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Integer> {
    List<ProductVariation> findAll(); // Fetch all variations
    List<ProductVariation> findByProductIdProductId(Integer productId); // Fetch variations by productId
//    ProductVariation findById();
    // Truy vấn để lấy màu sắc của sản phẩm
    @Query("SELECT DISTINCT pv.color FROM ProductVariation pv WHERE pv.productId.productId = ?1")
    List<String> findColorsByProductId(Integer productId);

    // Truy vấn để lấy kích thước của sản phẩm
    @Query("SELECT DISTINCT pv.size FROM ProductVariation pv WHERE pv.productId.productId = ?1")
    List<String> findSizesByProductId(Integer productId);

}
