package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.dto.ProductVariationDTO;
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


    List<ProductDTO> getAllProducts();

//    List<ProductDTO> getAllProductDTO();

    List<Products> getLatest5Products();

    List<ProductImage> getProductImagesByProductId(Integer productId);

    ProductVariationDTO getProductVariationsByProductId(Integer productId);

//    ProductVariationDTO getProductVariationByProductIdAndVariationId(Integer productId, Integer variationId);

    Products getProductById(Integer productId);

    List<ProductDTO> searchProducts(String query);

    ProductDTO getProductDetailById(Integer productId);

    List<ProductVariationDTO> getProductsByCategory(Integer categoryId);

    ProductVariationDTO getProductByVariationId(Integer variationId);

    List<ProductVariationDTO> getAllProductVariation();

    ProductDTO getProductDTOById(Integer productId);
}