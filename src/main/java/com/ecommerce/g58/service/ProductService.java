package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductVariationDTO;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Products;

import java.util.List;

public interface ProductService {

    // Fetch all products
    List<ProductDTO> getProductDetails();

    // Fetch product details for search
    List<ProductDTO> getSearchProduct();

    // Fetch a product variation by its ID
    ProductVariation getProductVariationById(Integer variationId);

    // Fetch all products (ProductDTO)
    List<ProductDTO> getAllProducts();

    // Fetch product by ID
    Products getProductById(Integer productId);

    // Search for products by name
    List<ProductDTO> searchProducts(String query);

    // Fetch product variations by category ID
    List<ProductVariationDTO> getProductsByCategory(Integer categoryId);

    List<ProductVariationDTO> getAllDistinctProducts();

    List<ProductVariationDTO> getProductDetailById(Integer productId);
}
