package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public interface ProductService {
    List<ProductDTO> getProductDetails();

    List<ProductDTO> getSearchProduct();

    ProductVariation getProductVariationById(Integer variationId);

    List<Products> getAllProducts();

    List<Products> getLatest5Products();

    List<ProductImage> getProductImagesByProductId(Integer productId);

    List<ProductVariation> getProductVariationsByProductId(Integer productId);

    Products getProductById(Integer productId);

    List<ProductDTO> searchProducts(String query);
}