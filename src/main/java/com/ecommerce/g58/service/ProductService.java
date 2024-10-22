package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public interface ProductService {
    List<ProductDTO> getProductDetails();

    List<Products> getProductsByCategory(Long categoryId);

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

    ProductDetailDTO getProductDetailByProductIdAndColorId(Integer productId, Integer colorId);

    List<Color> getAvailableColors(Integer productId);

    List<String> getAvailableSizesByProductIdAndColorId(Integer productId, Integer colorId);

}