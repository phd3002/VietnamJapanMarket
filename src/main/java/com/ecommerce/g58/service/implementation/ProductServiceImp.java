package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {
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

            // Mapping the fields from the result
            productDTO.setProductId((Integer) result[0]);        // productId
            productDTO.setThumbnail((String) result[1]);         // thumbnail
            productDTO.setProductName((String) result[2]);       // productName
            productDTO.setPrice((Integer) result[3]);            // price
            productDTO.setVariationId((Integer) result[4]);        // variationId
            productDTO.setImageId((Integer) result[5]);          // imageId

            productDetails.add(productDTO);
        }

        return productDetails;
    }

    @Override
    public ProductDetailDTO getProductDetailByProductIdAndVariationId(Integer productId, Integer variationId) {
        // Directly get the ProductDetailDTO from the repository
        return productRepository.findProductDetailByProductIdAndVariationId(productId, variationId);
    }

    public List<Products> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Fetch product details using a native query that joins products, product variations, and product images.
     * This query returns the product name, thumbnail, and price.
     */
    public List<ProductDTO> getSearchProduct() {
        List<Object[]> results = productRepository.findProductDetailsNative();
        return results.stream().map(result ->
                new ProductDTO(
                        (String) result[2],  // productName
                        (result[1] != null ? (String) result[1] : "default-image.png"),  // Handle null thumbnail
                        (Integer) result[3]  // price
                )
        ).collect(Collectors.toList());
    }

    // Method to fetch ProductVariation by its ID
    public ProductVariation getProductVariationById(Integer variationId) {
        return productVariationRepository.findById(variationId)
                .orElseThrow(() -> new IllegalArgumentException("Product variation not found with id: " + variationId));
    }


    //    public List<ProductDTO> findProductDetails() {
//        return productRepository.findProductDetails();
//    }
    public List<Products> getAllProducts() {
        return productRepository.findAll(); // Fetch all products
    }

    @Override
    public Page<Products> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
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

    public Products getProductById(Integer productId) {
        return productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found!")); // Fetch product by id
    }

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

    @Override
    public ProductDetailDTO getProductDetailByProductIdAndColorId(Integer productId, Integer colorId) {
        ProductDetailDTO productDetail = productRepository.findProductDetailByProductIdAndColorId(productId, colorId);

        if (productDetail != null) {
            return productDetail;
        } else {
            throw new EntityNotFoundException("Product not found for ID: " + productId + " and Color ID: " + colorId);
        }
    }

    public List<Color> getAvailableColors(Integer productId) {
        return productRepository.findAvailableColorsByProductId(productId);
    }

    @Override
    public List<String> getAvailableSizesByProductIdAndColorId(Integer productId, Integer colorId) {
        return productRepository.findSizesByProductIdAndColorId(productId, colorId);
    }

    @Override
    public List<Products> getProductsByStoreId(Stores storeId) {
        return productRepository.findByStoreId(storeId);
    }

}


