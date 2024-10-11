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
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    /**
     * Fetch product details using a native query that joins products, product variations, and product images.
     * This query returns the product name, thumbnail, and price.
     */
    public List<ProductDTO> getProductDetails() {
        List<Object[]> results = productRepository.findProductDetailsNative();
        return results.stream().map(result ->
                new ProductDTO(
                        (String) result[2],  // productName
                        (result[1] != null ? (String) result[1] : "default-image.png"),  // Handle null thumbnail
                        (Integer) result[3]  // price
                )
        ).collect(Collectors.toList());
    }

    /**
     * Fetch all products from the product repository.
     * @return List of Products
     */
    public List<Products> getAllProducts() {
        return productRepository.findAll(); // Fetch all products
    }

    /**
     * Fetch the latest 5 products ordered by creation date.
     * @return List of Products
     */
    public List<Products> getLatest5Products() {
        return productRepository.findTop5ByOrderByCreatedAtDesc(); // Fetch latest 5 products
    }

    /**
     * Fetch product images by the productId from the product image repository.
     * @param productId the product ID
     * @return List of ProductImage
     */
    public List<ProductImage> getProductImagesByProductId(Integer productId) {
        return productImageRepository.findByProductProductId(productId); // Fetch product images by product ID
    }

    /**
     * Fetch product variations by productId from the product variation repository.
     * @param productId the product ID
     * @return List of ProductVariation
     */
    public List<ProductVariation> getProductVariationsByProductId(Integer productId) {
        return productVariationRepository.findByProductIdProductId(productId); // Fetch product variations by product ID
    }

    /**
     * Fetch a single product by its ID.
     * @param productId the product ID
     * @return Products
     */
    public Products getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!")); // Fetch product by id
    }

    // Method to fetch ProductVariation by its ID
    public ProductVariation getProductVariationById(Integer variationId) {
        return productVariationRepository.findById(variationId)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + variationId));
    }

    /**
     * Search for products by name, handling the search query case insensitively.
     * @param query the search query string
     * @return List of ProductDTO
     */
    public List<ProductDTO> searchProducts(String query) {
        // Search the products by name using the repository method
        List<Products> products = productRepository.findByProductNameContainingIgnoreCase(query);

        // Convert the Products to ProductDTO and return them
        return products.stream()
                .map(product -> {
                    // Fetch the thumbnail from ProductImage entity based on product id
                    List<ProductImage> productImages = productImageRepository.findByProductProductId(product.getProductId());

                    // Use the first image's thumbnail if available
                    String thumbnail = productImages.isEmpty() ? "default-image.png" : productImages.get(0).getThumbnail();

                    return new ProductDTO(
                            product.getProductName(),
                            thumbnail,  // Fetch the thumbnail from ProductImage
                            product.getPrice()
                    );
                })
                .collect(Collectors.toList());
    }
}