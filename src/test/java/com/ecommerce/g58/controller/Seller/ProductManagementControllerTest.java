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

    // testAddProductFull tc1
    @Test
    void testAddProductFull_tc1() throws Exception {
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

    // testAddProductFull tc2
    @Test
    void testAddProductFull_tc2() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc3
    @Test
    void testAddProductFull_tc3() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc4
    @Test
    void testAddProductFull_tc4() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc5
    @Test
    void testAddProductFull_tc5() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc6
    @Test
    void testAddProductFull_tc6() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc7
    @Test
    void testAddProductFull_tc7() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc8
    @Test
    void testAddProductFull_tc8() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc9
    @Test
    void testAddProductFull_tc9() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc10
    @Test
    void testAddProductFull_tc10() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc11
    @Test
    void testAddProductFull_tc11() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc12
    @Test
    void testAddProductFull_tc12() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc13
    @Test
    void testAddProductFull_tc13() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc14
    @Test
    void testAddProductFull_tc14() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc15
    @Test
    void testAddProductFull_tc15() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc16
    @Test
    void testAddProductFull_tc16() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc17
    @Test
    void testAddProductFull_tc17() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc18
    @Test
    void testAddProductFull_tc18() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(19999);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc19
    @Test
    void testAddProductFull_tc19() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc20
    @Test
    void testAddProductFull_tc20() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc21
    @Test
    void testAddProductFull_tc21() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc22
    @Test
    void testAddProductFull_tc22() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc23
    @Test
    void testAddProductFull_tc23() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc24
    @Test
    void testAddProductFull_tc24() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc25
    @Test
    void testAddProductFull_tc25() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc26
    @Test
    void testAddProductFull_tc26() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc27
    @Test
    void testAddProductFull_tc27() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(50000001);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc28
    @Test
    void testAddProductFull_tc28() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc29
    @Test
    void testAddProductFull_tc29() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc30
    @Test
    void testAddProductFull_tc30() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc31
    @Test
    void testAddProductFull_tc31() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc32
    @Test
    void testAddProductFull_tc32() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc33
    @Test
    void testAddProductFull_tc33() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc34
    @Test
    void testAddProductFull_tc34() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc35
    @Test
    void testAddProductFull_tc35() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc36
    @Test
    void testAddProductFull_tc36() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(null);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc37
    @Test
    void testAddProductFull_tc37() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc38
    @Test
    void testAddProductFull_tc38() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc39
    @Test
    void testAddProductFull_tc39() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc40
    @Test
    void testAddProductFull_tc40() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc41
    @Test
    void testAddProductFull_tc41() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc42
    @Test
    void testAddProductFull_tc42() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc43
    @Test
    void testAddProductFull_tc43() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc44
    @Test
    void testAddProductFull_tc44() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc45
    @Test
    void testAddProductFull_tc45() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(0.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc46
    @Test
    void testAddProductFull_tc46() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc47
    @Test
    void testAddProductFull_tc47() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc48
    @Test
    void testAddProductFull_tc48() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc49
    @Test
    void testAddProductFull_tc49() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc50
    @Test
    void testAddProductFull_tc50() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc51
    @Test
    void testAddProductFull_tc51() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc52
    @Test
    void testAddProductFull_tc52() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc53
    @Test
    void testAddProductFull_tc53() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc54
    @Test
    void testAddProductFull_tc54() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(20.1f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc55
    @Test
    void testAddProductFull_tc55() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên hình ảnh không được để trống và không được vượt quá 255 ký tự.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc56
    @Test
    void testAddProductFull_tc56() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc57
    @Test
    void testAddProductFull_tc57() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc58
    @Test
    void testAddProductFull_tc58() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc59
    @Test
    void testAddProductFull_tc59() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc60
    @Test
    void testAddProductFull_tc60() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc61
    @Test
    void testAddProductFull_tc61() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc62
    @Test
    void testAddProductFull_tc62() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc63
    @Test
    void testAddProductFull_tc63() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quầaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc64
    @Test
    void testAddProductFull_tc64() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên hình ảnh không được để trống và không được vượt quá 255 ký tự.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc65
    @Test
    void testAddProductFull_tc65() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc66
    @Test
    void testAddProductFull_tc66() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc67
    @Test
    void testAddProductFull_tc67() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc68
    @Test
    void testAddProductFull_tc68() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc69
    @Test
    void testAddProductFull_tc69() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc70
    @Test
    void testAddProductFull_tc70() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc71
    @Test
    void testAddProductFull_tc71() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc72
    @Test
    void testAddProductFull_tc72() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("");
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation,productImage.getImageName() , session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc73
    @Test
    void testAddProductFull_tc73() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessageVar", "Số lượng sản phẩm phải lớn hơn hoặc bằng 0.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc74
    @Test
    void testAddProductFull_tc74() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc75
    @Test
    void testAddProductFull_tc75() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc76
    @Test
    void testAddProductFull_tc76() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc77
    @Test
    void testAddProductFull_tc77() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc78
    @Test
    void testAddProductFull_tc78() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc79
    @Test
    void testAddProductFull_tc79() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc80
    @Test
    void testAddProductFull_tc80() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc81
    @Test
    void testAddProductFull_tc81() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-1);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc82
    @Test
    void testAddProductFull_tc82() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessageVar", "Số lượng sản phẩm phải lớn hơn hoặc bằng 0.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc83
    @Test
    void testAddProductFull_tc83() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc84
    @Test
    void testAddProductFull_tc84() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc85
    @Test
    void testAddProductFull_tc85() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc86
    @Test
    void testAddProductFull_tc86() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc87
    @Test
    void testAddProductFull_tc87() throws Exception {
        Products product = new Products();
        product.setProductName("Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc88
    @Test
    void testAddProductFull_tc88() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc89
    @Test
    void testAddProductFull_tc89() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

    // testAddProductFull tc90
    @Test
    void testAddProductFull_tc90() throws Exception {
        Products product = new Products();
        product.setProductName("");
        product.setProductDescription("");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        Stores store = new Stores();
        store.setStoreId(1);
        Integer storeId = 1;
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage,
                productVariation, "testImage", session, model, redirectAttributes);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
        assertEquals("redirect:/addProductForm2/" + storeId, result);
    }

//----------------------------------------------------------------------------------------------------------------------------------

    // test updateProduct tc1
    @Test
    void testUpdateProduct_tc1() {
    // Arrange
    Integer productId = 1;
    String productName = "Quần";
    String productDescription = "dài ống";
    Integer price = 100000;
    float weight = 1.0f;
    Products product = new Products();
    when(productService.findById(productId)).thenReturn(Optional.of(product));
    String result = ProductManagementController.updateProduct(productId, productName, productDescription, price, weight, redirectAttributes);
    assertEquals("redirect:/edit-product/" + productId, result);
    verify(productService, times(1)).saveProduct(product);
    verify(redirectAttributes, times(1)).addFlashAttribute("message", "Cập nhật sản phẩm thành công.");
}

    // test updateProduct tc2
    @Test
    void testUpdateProduct_tc2() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc3
    @Test
    void testUpdateProduct_tc3() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc4
    @Test
    void testUpdateProduct_tc4() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc5
    @Test
    void testUpdateProduct_tc5() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc6
    @Test
    void testUpdateProduct_tc6() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc7
    @Test
    void testUpdateProduct_tc7() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc8
    @Test
    void testUpdateProduct_tc8() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc9
    @Test
    void testUpdateProduct_tc9() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc10
    @Test
    void testUpdateProduct_tc10() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
    }

    // test updateProduct tc11
    @Test
    void testUpdateProduct_tc11() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc12
    @Test
    void testUpdateProduct_tc12() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc13
    @Test
    void testUpdateProduct_tc13() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc14
    @Test
    void testUpdateProduct_tc14() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc15
    @Test
    void testUpdateProduct_tc15() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc16
    @Test
    void testUpdateProduct_tc16() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc17
    @Test
    void testUpdateProduct_tc17() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc18
    @Test
    void testUpdateProduct_tc18() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        int price = 19999;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc19
    @Test
    void testUpdateProduct_tc19() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
    }

    // test updateProduct tc20
    @Test
    void testUpdateProduct_tc20() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc21
    @Test
    void testUpdateProduct_tc21() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc22
    @Test
    void testUpdateProduct_tc22() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc23
    @Test
    void testUpdateProduct_tc23() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc24
    @Test
    void testUpdateProduct_tc24() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc25
    @Test
    void testUpdateProduct_tc25() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc26
    @Test
    void testUpdateProduct_tc26() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc27
    @Test
    void testUpdateProduct_tc27() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        int price = 50000001;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc28
    @Test
    void testUpdateProduct_tc28() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
    }

    // test updateProduct tc29
    @Test
    void testUpdateProduct_tc29() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc30
    @Test
    void testUpdateProduct_tc30() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc31
    @Test
    void testUpdateProduct_tc31() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc32
    @Test
    void testUpdateProduct_tc32() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc33
    @Test
    void testUpdateProduct_tc33() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc34
    @Test
    void testUpdateProduct_tc34() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc35
    @Test
    void testUpdateProduct_tc35() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc36
    @Test
    void testUpdateProduct_tc36() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        Integer price = null;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc37
    @Test
    void testUpdateProduct_tc37() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
    }

    // test updateProduct tc38
    @Test
    void testUpdateProduct_tc38() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc39
    @Test
    void testUpdateProduct_tc39() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc40
    @Test
    void testUpdateProduct_tc40() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc41
    @Test
    void testUpdateProduct_tc41() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc42
    @Test
    void testUpdateProduct_tc42() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc43
    @Test
    void testUpdateProduct_tc43() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc44
    @Test
    void testUpdateProduct_t44() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc45
    @Test
    void testUpdateProduct_tc45() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        int price = 50000;
        float weight = 0.0f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }


    // test updateProduct tc46
    @Test
    void testUpdateProduct_tc46() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
    }

    // test updateProduct tc47
    @Test
    void testUpdateProduct_tc47() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc48
    @Test
    void testUpdateProduct_tc48() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc49
    @Test
    void testUpdateProduct_tc49() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ống";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc50
    @Test
    void testUpdateProduct_tc50() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc51
    @Test
    void testUpdateProduct_tc51() {
        Integer productId = 1;
        String invalidName = "Quầnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn" +
                "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn";
        String description = "";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc52
    @Test
    void testUpdateProduct_tc52() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ống";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc53
    @Test
    void testUpdateProduct_t53() {
        Integer productId = 1;
        String invalidName = "";
        String description = "dài ốngggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +
                "gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc54
    @Test
    void testUpdateProduct_tc54() {
        Integer productId = 1;
        String invalidName = "";
        String description = "";
        int price = 50000;
        float weight = 20.1f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
    }

    // test updateProduct tc55
    @Test
    void testUpdateProduct_tc55() {
        Integer productId = 1;
        when(productService.findById(productId)).thenReturn(Optional.empty());
        String result = ProductManagementController.updateProduct(productId, "Valid Name", "Valid Description", 50000, 2.5f, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không tìm thấy sản phẩm.");
    }


}