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

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    public List<ProductDTO> getProductDetails() {
        List<Object[]> results = productRepository.findProductDetailsNative();
        List<ProductDTO> productDetails = new ArrayList<>();

        for (Object[] result : results) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setThumbnail((String) result[1]);
            productDTO.setProductName((String) result[2]);
            productDTO.setPrice((BigDecimal) result[3]);

            productDetails.add(productDTO);
        }

        return productDetails;
    }

    //    public List<ProductDTO> findProductDetails() {
//        return productRepository.findProductDetails();
//    }
    public List<Products> getAllProducts() {
        return productRepository.findAll(); // Fetch all products
    }

    public List<Products> getLatest5Products() {
        return productRepository.findTop5ByOrderByCreatedAtDesc(); // Fetch latest 5 products
    }

    // Fetch product images by productId
    public List<ProductImage> getProductImagesByProductId(Integer productId) {
        return productImageRepository.findByProductProductId(productId);
    }

    // Fetch product variations by productId
    public List<ProductVariation> getProductVariationsByProductId(Integer productId) {
        return productVariationRepository.findByProductIdProductId(productId);
    }

    public Products getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found!")); // Fetch product by id
    }


}