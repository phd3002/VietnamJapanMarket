//package com.ecommerce.g58.api.imp;
//
//import com.ecommerce.g58.dto.ProductDTO;
//import com.ecommerce.g58.entity.ProductImage;
//import com.ecommerce.g58.entity.ProductVariation;
//import com.ecommerce.g58.entity.Products;
//import com.ecommerce.g58.repository.ProductImageRepository;
//import com.ecommerce.g58.repository.ProductRepository;
//import com.ecommerce.g58.repository.ProductVariationRepository;
//import com.ecommerce.g58.service.implementation.ProductServiceImp;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ContextConfiguration(classes = {ProductServiceImp.class})
//public class ProductServiceImpTest {
//
//    @Autowired
//    private ProductServiceImp productService;
//
//    @MockBean
//    private ProductRepository productRepository;
//
//    @MockBean
//    private ProductImageRepository productImageRepository;
//
//    @MockBean
//    private ProductVariationRepository productVariationRepository;
//
//    private Products product;
//    private ProductVariation productVariation;
//    private ProductImage productImage;
//
//    @BeforeEach
//    public void setUp() {
//        product = new Products();
//        product.setProductId(1);
//        product.setProductName("phone");
//        product.setPrice(100);
//
//        productVariation = new ProductVariation();
//        productVariation.setVariationId(1);
//        productVariation.setProductId(product);
//
//        productImage = new ProductImage();
//        productImage.setImageId(1);
//        productImage.setProduct(product);
//        productImage.setThumbnail("test-opposamsung.png");
//    }
//
//    @Test
//    public void testGetProductDetails() {
//        List<Object[]> results = new ArrayList<>();
//        results.add(new Object[]{1, "test-opposamsung.png", "phone", 100, 1, 1});
//        when(productRepository.findProductDetailsNative()).thenReturn(results);
//
//        List<ProductDTO> productDetails = productService.getProductDetails();
//        assertNotNull(productDetails);
//        assertEquals(1, productDetails.size());
//        assertEquals("phone", productDetails.get(0).getProductName());
//    }
//
//    @Test
//    public void testGetSearchProduct() {
//        List<Object[]> results = new ArrayList<>();
//        results.add(new Object[]{1, "test-opposamsung.png", "phone", 100});
//        when(productRepository.findProductDetailsNative()).thenReturn(results);
//
//        List<ProductDTO> searchResults = productService.getSearchProduct();
//        assertNotNull(searchResults);
//        assertEquals(1, searchResults.size());
//        assertEquals("phone", searchResults.get(0).getProductName());
//    }
//
//    @Test
//    public void testGetProductVariationById() {
//        when(productVariationRepository.findById(1)).thenReturn(Optional.of(productVariation));
//        ProductVariation foundVariation = productService.getProductVariationById(1);
//        assertNotNull(foundVariation);
//        assertEquals(1, foundVariation.getVariationId());
//    }
//
//    @Test
//    public void testGetAllProducts() {
//        List<Products> products = new ArrayList<>();
//        products.add(product);
//        when(productRepository.findAll()).thenReturn(products);
//
//        List<Products> allProducts = productService.getAllProducts();
//        assertNotNull(allProducts);
//        assertEquals(1, allProducts.size());
//        assertEquals("phone", allProducts.get(0).getProductName());
//    }
//
//    @Test
//    public void testGetLatest5Products() {
//        List<Products> products = new ArrayList<>();
//        products.add(product);
//        when(productRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(products);
//
//        List<Products> latestProducts = productService.getLatest5Products();
//        assertNotNull(latestProducts);
//        assertEquals(1, latestProducts.size());
//        assertEquals("phone", latestProducts.get(0).getProductName());
//    }
//
//    @Test
//    public void testGetProductImagesByProductId() {
//        List<ProductImage> images = new ArrayList<>();
//        images.add(productImage);
//        when(productImageRepository.findByProductProductId(1)).thenReturn(images);
//
//        List<ProductImage> productImages = productService.getProductImagesByProductId(1);
//        assertNotNull(productImages);
//        assertEquals(1, productImages.size());
//        assertEquals("test-opposamsung.png", productImages.get(0).getThumbnail());
//    }
//
//    @Test
//    public void testGetProductVariationsByProductId() {
//        List<ProductVariation> variations = new ArrayList<>();
//        variations.add(productVariation);
//        when(productVariationRepository.findByProductIdProductId(1)).thenReturn(variations);
//
//        List<ProductVariation> productVariations = productService.getProductVariationsByProductId(1);
//        assertNotNull(productVariations);
//        assertEquals(1, productVariations.size());
//        assertEquals(1, productVariations.get(0).getVariationId());
//    }
//
//    @Test
//    public void testGetProductById() {
//        when(productRepository.findById(1)).thenReturn(Optional.of(product));
//        Products foundProduct = productService.getProductById(1);
//        assertNotNull(foundProduct);
//        assertEquals("phone", foundProduct.getProductName());
//    }
//
////    @Test
////    public void testSearchProducts() {
////        List<Products> products = new ArrayList<>();
////        products.add(product);
////        when(productRepository.findByProductNameContainingIgnoreCase("Test")).thenReturn(products);
////        when(productImageRepository.findByProductProductId(1)).thenReturn(List.of(productImage));
////
////        List<ProductDTO> searchResults = productService.searchProducts("Test");
////        assertNotNull(searchResults);
////        assertEquals(1, searchResults.size());
////        assertEquals("phone", searchResults.get(0).getProductName());
////    }
//}