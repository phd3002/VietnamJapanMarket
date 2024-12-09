package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.dto.ProductWithVariationsDTO;
import com.ecommerce.g58.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {
    List<ProductDTO> getProductDetails();
    Page<Products> getAllProducts(Pageable pageable);
    Page<Products>getProductsByCategory(Long categoryId, Pageable pageable);

    List<ProductDTO> getSearchProduct();

    ProductVariation getProductVariationById(Integer variationId);

    ProductDetailDTO getProductDetailByProductIdAndVariationId(Integer productId, Integer variationId);

    List<Products> getAllProducts();

    Page<Products> findAllProducts(Pageable pageable);

    List<Products> getLatest5Products();

    List<ProductImage> getProductImagesByProductId(Integer productId);

    List<ProductVariation> getProductVariationsByProductId(Integer productId);

    Products getProductById(Integer productId);

    List<ProductDTO> searchProducts(String query);

    List<ProductDetailDTO> getProductDetailByProductIdAndColorId(Integer productId, Integer colorId);

    List<Color> getAvailableColors(Integer productId);

    List<String> getAvailableSizesByProductIdAndColorId(Integer productId, Integer colorId);

    Page<Products> getProductsByStoreId( Pageable pageable);

    void saveProduct(Products product);

    void deleteProductById(Integer productId);

    void addProduct(Products product);

    void addProductImage(ProductImage productImage);

    void addProductVariation(ProductVariation productVariation);

    Products findProductById(Products productId);

    Optional<Products> findById(Integer productId);

    ProductVariation findProductVariationById(int variationId);

    Products getMaxProductId();

    ProductImage getMaxImageId();
}