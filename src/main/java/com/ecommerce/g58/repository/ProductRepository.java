package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductVariationDTO;
import com.ecommerce.g58.entity.ProductVariation;
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

    // Fetch all products for product-list with their main thumbnail and price
    @Query("SELECT new com.ecommerce.g58.dto.ProductDTO(p.productId, p.productName, pi.thumbnail, p.price) " +
            "FROM Products p " +
            "JOIN p.productVariations pv " +
            "JOIN pv.imageId pi ")
    List<ProductDTO> findAllProducts();

    @Query("SELECT new com.ecommerce.g58.dto.ProductVariationDTO(p.productId, pi.thumbnail, pi.image1, pi.image2, pi.image3, pi.image4, c.categoryName, p.productName, p.price, pv.stock) " +
            "FROM Products p " +
            "JOIN ProductVariation pv ON pv.productId.productId = p.productId " +
            "JOIN p.categoryId c " +
            "JOIN pv.imageId pi " +
            "GROUP BY p.productId, pi.thumbnail, pi.image1, pi.image2, pi.image3, pi.image4, c.categoryName, p.productName, p.price, pv.stock")
    List<ProductVariationDTO> getAllDistinctProducts();




    @Query("SELECT new com.ecommerce.g58.dto.ProductVariationDTO(p.productId, pi.thumbnail, pi.image1, pi.image2, pi.image3, pi.image4, c.categoryName, p.productName, col.colorName, s.sizeName, p.price, pv.stock) " +
            "FROM Products p " +
            "JOIN p.categoryId c " +
            "JOIN ProductVariation pv ON pv.productId.productId = p.productId " +
            "JOIN pv.imageId pi " +
            "JOIN pv.size s " +
            "JOIN pv.color col " +
            "WHERE p.productId = :productId")
    List<ProductVariationDTO> findAllProductVariationsById(@Param("productId") Integer productId);





    // Query to fetch product details (including description) for product detail view
    @Query("SELECT new com.ecommerce.g58.dto.ProductDTO(p.productId, p.productName, pi.thumbnail, p.price, p.productDescription) " +
            "FROM Products p " +
            "JOIN p.productVariations pv " +
            "JOIN pv.imageId pi " +
            "WHERE p.productId = ?1")
    ProductDTO findProductById(Integer productId);
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
    List<Products> findByCategoryId(@Param("categoryId") Integer categoryId);

}
