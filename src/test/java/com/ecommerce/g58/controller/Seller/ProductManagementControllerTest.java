package com.ecommerce.g58.controller.Seller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.g58.controller.Seller.StoreController;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.CategoriesService;
import com.ecommerce.g58.service.StoreService;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class ProductManagementControllerTest {
    @InjectMocks
    private ProductManagementController ProductManagementController;

    @Mock
    private StoreService storeService;

    @Mock
    private ProductService productService;

    @Mock
    private FileS3Service fileS3Service;

    @Mock
    private CategoriesService categoriesService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private MultipartFile thumbnail, firstImage, secondImage, thirdImage;

    private Products product;
    private ProductVariation productVariation;
    private Stores store;
    private ProductImage productImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddProductFull_Success() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quần");
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(fileS3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://example.com/image.jpg");
        when(productService.getMaxProductId()).thenReturn(product);
        when(productService.getMaxImageId()).thenReturn(productImage);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(productService, times(1)).addProduct(product);
        verify(fileS3Service, times(4)).uploadFile(any(MultipartFile.class));
        verify(productService, times(1)).addProductImage(any(ProductImage.class));
        verify(productService, times(1)).addProductVariation(productVariation);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }
}