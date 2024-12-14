package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Products, Integer> {
    void deleteByProductId(Integer productId);

    Products findTopByOrderByProductIdDesc();

    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Products> find12RandomProducts();

    @Query(value = "SELECT * FROM products ORDER BY RAND()", nativeQuery = true)
    Page<Products> findAllByOrderByRandom(Pageable pageable);

    List<Products> findAll();

    List<Products> findByStoreId(Stores storeId);


    @Query("SELECT new com.ecommerce.g58.dto.ProductDetailDTO(p.productId, p.productName, p.price, p.categoryId.categoryId, p.storeId.storeId, v.size.sizeName, v.color.colorName, v.stock, v.imageId.imageId) " +
            "FROM Products p LEFT JOIN ProductVariation v ON p.productId = v.productId.productId " +
            "WHERE p.storeId = :storeId")
    List<ProductDetailDTO> findAllProductDetailsByStoreId(@Param("storeId") Stores storeId);

//    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.productVariations pv WHERE p.storeId = :storeId")
//    List<Products> findByStoreIdWithVariations(@Param("storeId") Stores storeId);

    @Query("SELECT p, pv FROM Products p LEFT JOIN FETCH p.productVariations pv WHERE p.storeId = :storeId")
    List<Products> findByStoreIdWithVariations(@Param("storeId") Stores storeId);

    List<Products> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT new com.ecommerce.g58.dto.ProductDetailDTO(" +
            "p.productId, p.productName, p.productDescription, p.price, p.weight, " +
            "pv.variationId, s.sizeName, c.colorName, pv.stock, pi.thumbnail, p.storeId.storeId, p.storeId.storeMail) " +
            "FROM Products p " +
            "JOIN ProductVariation pv ON p.productId = pv.productId.productId " +
            "JOIN Size s ON pv.size.sizeId = s.sizeId " +
            "JOIN Color c ON pv.color.colorId = c.colorId " +
            "JOIN ProductImage pi ON pv.imageId.imageId = pi.imageId " +
            "WHERE p.productId = :productId AND pv.variationId = :variationId")
    ProductDetailDTO findProductDetailByProductIdAndVariationId(@Param("productId") Integer productId, @Param("variationId") Integer variationId);

    @Query("SELECT new com.ecommerce.g58.dto.ProductDetailDTO("
            + "p.productId, p.productName, p.productDescription, p.price, p.weight, "
            + "pv.variationId, s.sizeName, c.colorName, pv.stock, "
            + "pi.thumbnail, pi.image1, pi.image2, pi.image3, pi.image4, p.storeId.storeId) "
            + "FROM Products p "
            + "JOIN ProductVariation pv ON p.productId = pv.productId.productId "
            + "JOIN ProductImage pi ON pv.imageId.imageId = pi.imageId "
            + "JOIN Size s ON pv.size.sizeId = s.sizeId "
            + "JOIN Color c ON pv.color.colorId = c.colorId "
            + "WHERE p.productId = :productId AND c.colorId = :colorId "
            + "ORDER BY s.sizeId ASC")
    List<ProductDetailDTO> findProductDetailsByProductIdAndColorId(
            @Param("productId") Integer productId,
            @Param("colorId") Integer colorId);



    @Query("SELECT DISTINCT c FROM Color c JOIN ProductVariation pv ON pv.color.colorId = c.colorId WHERE pv.productId.productId = :productId")
    List<Color> findAvailableColorsByProductId(@Param("productId") Integer productId);

    @Query(value = "SELECT DISTINCT s.size_name " +
            "FROM product_variation pv " +
            "JOIN size s ON pv.size_id = s.size_id " +
            "WHERE pv.product_id = :productId AND pv.color_id = :colorId", nativeQuery = true)
    List<String> findSizesByProductIdAndColorId(@Param("productId") Integer productId,
                                                @Param("colorId") Integer colorId);


    @Query(value = "SELECT p.product_id, p.product_name, p.product_description, p.price, p.weight, "
            + "pv.variation_id, s.size_name, c.color_name, pv.stock, "
            + "pi.thumbnail, pi.image_1, pi.image_2, pi.image_3, pi.image_4 "
            + "FROM products p "
            + "JOIN product_variation pv ON p.product_id = pv.product_id "
            + "JOIN product_image pi ON pv.image_id = pi.image_id "
            + "JOIN size s ON pv.size_id = s.size_id "
            + "JOIN color c ON pv.color_id = c.color_id "
            + "WHERE p.product_id = :productId", nativeQuery = true)
    Object[] findProductDetailByProductIdNative(@Param("productId") Integer productId);


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

    @Query(value = "SELECT DISTINCT p.* " +
            "FROM products p " +
            "JOIN product_variation pv ON p.product_id = pv.product_id " +
            "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) AND pv.stock > 0",
            countQuery = "SELECT COUNT(DISTINCT p.product_id) " +
                    "FROM products p " +
                    "JOIN product_variation pv ON p.product_id = pv.product_id " +
                    "WHERE (:categoryId IS NULL OR p.category_id = :categoryId) AND pv.stock > 0",
            nativeQuery = true)
    Page<Products> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    Page<Products> findByStoreIdStoreId(Integer storeId, Pageable pageable);

}
