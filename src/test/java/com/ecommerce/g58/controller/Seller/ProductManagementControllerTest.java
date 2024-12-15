package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.service.CategoriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.StoreService;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;


@RunWith(MockitoJUnitRunner.class)
class ProductManagementControllerTest {
    @InjectMocks
    private ProductManagementController ProductManagementController;

    @Mock
    private StoreService storeService;

    @Mock
    private ProductService productService;

    @Mock
    private CategoriesService categoriesService;

    @Mock
    private FileS3Service fileS3Service;

    @Mock
    private HttpSession session;

    @Mock
    private ProductVariationRepository productVariationRepository;

    @Mock
    private Model model;


    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private MultipartFile thumbnail, firstImage, secondImage, thirdImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        session = mock(HttpSession.class);
        redirectAttributes = new RedirectAttributesModelMap();
        model = mock(Model.class);
    }

    // testAddProductFull tc1
    @Test
    void testAddProductFullSuccess() throws Exception {
        // Dữ liệu mẫu
        Integer storeId = 1;

        Products product = new Products();
        product.setProductName("Valid Product");
        product.setProductDescription("Valid Description");
        product.setPrice(30000);
        product.setWeight(1.5f);

        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(10);

        // Tạo các file hình ảnh mẫu
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MockMultipartFile firstImage = new MockMultipartFile("firstImage", "first.jpg", "image/jpeg", "first image data".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("secondImage", "second.jpg", "image/jpeg", "second image data".getBytes());
        MockMultipartFile thirdImage = new MockMultipartFile("thirdImage", "third.jpg", "image/jpeg", "third image data".getBytes());

        String imageName = "sampleImage";

        Stores store = new Stores();
        ProductImage productImage = new ProductImage();

        // Giả lập danh sách categories
        List<Categories> mockCategories = List.of(new Categories(), new Categories());

        // Giả lập các phương thức trả về giá trị mong đợi
        when(session.getAttribute("storeId")).thenReturn(storeId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(fileS3Service.uploadFile(thumbnail)).thenReturn("http://example.com/thumbnail.jpg");
        when(fileS3Service.uploadFile(firstImage)).thenReturn("http://example.com/first.jpg");
        when(fileS3Service.uploadFile(secondImage)).thenReturn("http://example.com/second.jpg");
        when(fileS3Service.uploadFile(thirdImage)).thenReturn("http://example.com/third.jpg");
        when(productService.getMaxProductId()).thenReturn(product);
        when(productService.getMaxImageId()).thenReturn(productImage);
        when(categoriesService.getAllCategories()).thenReturn(mockCategories);

        // Gọi phương thức
        String result = ProductManagementController.addProductFull(product, thumbnail, firstImage, secondImage, thirdImage, productVariation, imageName, session, model, redirectAttributes);

        // Kiểm tra kết quả trả về
        assertEquals("redirect:/addProductForm2/" + storeId, result);

        // Kiểm tra thông báo thành công được thêm vào redirectAttributes
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
        assertEquals("Thêm sản phẩm thành công!", redirectAttributes.getFlashAttributes().get("successMessage"));

        // Kiểm tra các phương thức được gọi đúng số lần
        verify(storeService, times(1)).findById(storeId);
        verify(productService, times(1)).addProduct(product);
        verify(fileS3Service, times(1)).uploadFile(thumbnail);
        verify(fileS3Service, times(1)).uploadFile(firstImage);
        verify(fileS3Service, times(1)).uploadFile(secondImage);
        verify(fileS3Service, times(1)).uploadFile(thirdImage);
        verify(productService, times(1)).addProductImage(any(ProductImage.class));
        verify(productService, times(1)).addProductVariation(productVariation);
        verify(categoriesService, times(1)).getAllCategories();
    }


    // testAddProductFull tc2
    @Test
    void testAddProductFull_tc2() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần".repeat(100));
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc3
    @Test
    void testAddProductFull_tc3() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("");
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc4
    @Test
    void testAddProductFull_tc4() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống".repeat(500));
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc5
    @Test
    void testAddProductFull_tc5() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("");
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc6
    @Test
    void testAddProductFull_tc6() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(19999);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    @Test
    void testAddProductFull_tc7() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(50000001);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc8
    @Test
    void testAddProductFull_tc8() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(null);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc9
    @Test
    void testAddProductFull_tc9() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(0.0f);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }


    // testAddProductFull tc10
    @Test
    void testAddProductFull_tc10() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(20.1f);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, new ProductVariation(), "imageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc11
    @Test
    void testAddProductFull_tc11() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Valid Product");
        product.setProductDescription("Valid Description");
        product.setPrice(30000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String imageName = "";
        String result = ProductManagementController.addProductFull(product, null, null, null, null, productVariation, imageName, session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Tên hình ảnh không được để trống và không được vượt quá 255 ký tự.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc12
    @Test
    void testAddProductFull_tc12() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Valid Product");
        product.setProductDescription("Valid Description");
        product.setPrice(30000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String imageName = "a".repeat(256);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, productVariation, imageName, session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Tên hình ảnh không được để trống và không được vượt quá 255 ký tự.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }

    // testAddProductFull tc13
    @Test
    void testAddProductFull_tc13() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Valid Product");
        product.setProductDescription("Valid Description");
        product.setPrice(30000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(-5);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, productVariation, "validImageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessageVar"));
        assertEquals("Số lượng sản phẩm phải lớn hơn hoặc bằng 0.", redirectAttributes.getFlashAttributes().get("errorMessageVar"));
        verify(productService, never()).addProductVariation(any(ProductVariation.class));
    }


    // testAddProductFull tc14
    @Test
    void testAddProductFull_tc14() {
        Integer storeId = 1;
        Products product = new Products();
        product.setProductName("Valid Product");
        product.setProductDescription("Valid Description");
        product.setPrice(30000);
        product.setWeight(1.0f);
        ProductVariation productVariation = new ProductVariation();
        productVariation.setStock(null);
        when(session.getAttribute("storeId")).thenReturn(storeId);
        String result = ProductManagementController.addProductFull(product, null, null, null, null, productVariation, "validImageName", session, model, redirectAttributes);
        assertEquals("redirect:/addProductForm2/" + storeId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessageVar"));
        assertEquals("Số lượng sản phẩm phải lớn hơn hoặc bằng 0.", redirectAttributes.getFlashAttributes().get("errorMessageVar"));
        verify(productService, never()).addProductVariation(any(ProductVariation.class));
    }


//----------------------------------------------------------------------------------------------------------------------------------

    // test updateProduct tc1
    @Test
    void testUpdateProduct_tc1() {
        Integer productId = 1;
        String productName = "Sản phẩm mới";
        String productDescription = "Mô tả sản phẩm mới";
        Integer price = 50000;
        float weight = 2.5f;
        Products product = new Products();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setProductDescription(productDescription);
        product.setPrice(price);
        product.setWeight(weight);
        when(productService.findById(productId)).thenReturn(Optional.of(product));
        String result = ProductManagementController.updateProduct(productId, productName, productDescription, price, weight, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("message"));
        assertEquals("Cập nhật sản phẩm thành công.", redirectAttributes.getFlashAttributes().get("message"));
        verify(productService, times(1)).saveProduct(product);
    }

    // test updateProduct tc2
    @Test
    void testUpdateProduct_tc2() {
        Integer productId = 1;
        String invalidName = "Quần".repeat(500);
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

    // test updateProduct tc3
    @Test
    void testUpdateProduct_tc3() {
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

    // test updateProduct tc4
    @Test
    void testUpdateProduct_tc4() {
        Integer productId = 1;
        String invalidName = "Quần";
        String description = "dài ống".repeat(500);
        int price = 50000;
        float weight = 2.5f;
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateProduct(productId, invalidName, description, price, weight, redirectAttributes
        );
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1))
                .addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
    }

    // test updateProduct tc5
    @Test
    void testUpdateProduct_tc5() {
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

    // test updateProduct tc6
    @Test
    void testUpdateProduct_tc6() {
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

    // test updateProduct tc7
    @Test
    void testUpdateProduct_tc7() {
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

    // test updateProduct tc8
    @Test
    void testUpdateProduct_tc8() {
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

    // test updateProduct tc9
    @Test
    void testUpdateProduct_tc9() {
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

    // test updateProduct tc10
    @Test
    void testUpdateProduct_tc10() {
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
    // test updateProduct tc11
    @Test
    void testUpdateProduct_tc11() {
        // Dữ liệu mẫu
        Integer productId = 999;
        String productName = "Sản phẩm mới";
        String productDescription = "Mô tả sản phẩm mới";
        Integer price = 50000;
        float weight = 2.5f;

        // Giả lập productService.findById trả về Optional.empty() (không tìm thấy sản phẩm)
        when(productService.findById(productId)).thenReturn(Optional.empty());

        // Gọi phương thức updateProduct trực tiếp
        String result =ProductManagementController.updateProduct(productId, productName, productDescription, price, weight, redirectAttributes);

        // Kiểm tra kết quả trả về
        assertEquals("redirect:/edit-product/" + productId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("error"));
        assertEquals("Không tìm thấy sản phẩm.", redirectAttributes.getFlashAttributes().get("error"));

        // Kiểm tra phương thức saveProduct không được gọi
        verify(productService, never()).saveProduct(any(Products.class));
    }
//-----------------------------------------------------------
    @Test
    void testUpdateStock_tc1() {
        Integer variationId = 1;
        Integer newStock = 1;
        ProductVariation variation = new ProductVariation();
        when(productService.findProductVariationById(variationId)).thenReturn(variation);
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateStock(variationId, newStock, mockRedirectAttributes);
        assertEquals("redirect:/seller-products", result);
        assertEquals(newStock, variation.getStock());
        verify(productVariationRepository, times(1)).save(variation);
        verify(mockRedirectAttributes, times(1)).addFlashAttribute("successMessage", "Cập nhật số lượng thành công.");
    }

    @Test
    void testUpdateStock_tc2() {
        Integer variationId = 1;
        Integer newStock = -1;
        ProductVariation variation = new ProductVariation();
        when(productService.findProductVariationById(variationId)).thenReturn(variation);
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        String result = ProductManagementController.updateStock(variationId, newStock, mockRedirectAttributes);
        assertEquals("redirect:/seller-products", result);
        verify(mockRedirectAttributes, times(1)).addFlashAttribute("errorMessage", "Số lượng mới không thể âm.");
        verify(productVariationRepository, never()).save(variation);
    }

    //--------------------------------------------------
    @Test
    void testAddProductVariation_tc1() throws Exception {
        Integer productId = 1;
        String imageName = "sampleImage";
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MockMultipartFile firstImage = new MockMultipartFile("firstImage", "first.jpg", "image/jpeg", "first image data".getBytes());
        MockMultipartFile secondImage = new MockMultipartFile("secondImage", "second.jpg", "image/jpeg", "second image data".getBytes());
        MockMultipartFile thirdImage = new MockMultipartFile("thirdImage", "third.jpg", "image/jpeg", "third image data".getBytes());
        Products product = new Products();
        product.setProductId(productId);
        ProductVariation productVariation = new ProductVariation();
        when(productService.findById(productId)).thenReturn(Optional.of(product));
        when(fileS3Service.uploadFile(thumbnail)).thenReturn("http://example.com/thumbnail.jpg");
        when(fileS3Service.uploadFile(firstImage)).thenReturn("http://example.com/first.jpg");
        when(fileS3Service.uploadFile(secondImage)).thenReturn("http://example.com/second.jpg");
        when(fileS3Service.uploadFile(thirdImage)).thenReturn("http://example.com/third.jpg");
        ProductImage maxImage = new ProductImage();
        maxImage.setImageId(1);
        when(productService.getMaxImageId()).thenReturn(maxImage);
        String result = ProductManagementController.addProductVariation(productId, productVariation, thumbnail, firstImage, secondImage, thirdImage, imageName, redirectAttributes, session);
        assertEquals("redirect:/seller-products", result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
        assertEquals("Thêm biến thể sản phẩm thành công!", redirectAttributes.getFlashAttributes().get("successMessage"));
        verify(productService, times(1)).findById(productId);
        verify(fileS3Service, times(1)).uploadFile(thumbnail);
        verify(fileS3Service, times(1)).uploadFile(firstImage);
        verify(fileS3Service, times(1)).uploadFile(secondImage);
        verify(fileS3Service, times(1)).uploadFile(thirdImage);
        verify(productService, times(1)).addProductImage(any(ProductImage.class));
        verify(productService, times(1)).addProductVariation(productVariation);
    }

    @Test
    void testAddProductVariationProduct_tc2() {
        Integer productId = 999;
        String imageName = "sampleImage";
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        ProductVariation productVariation = new ProductVariation();
        when(productService.findById(productId)).thenReturn(Optional.empty());
        when(session.getAttribute("storeId")).thenReturn(1);
        String result = ProductManagementController.addProductVariation(productId, productVariation, thumbnail, null, null, null, imageName, redirectAttributes, session);
        assertEquals("redirect:/seller-products/1", result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Không tìm thấy sản phẩm", redirectAttributes.getFlashAttributes().get("errorMessage"));
        verify(productService, times(1)).findById(productId);
        verifyNoInteractions(fileS3Service);
    }

    @Test
    void testAddProductVariationImageUploadFailure() throws Exception {
        Integer productId = 1;
        String imageName = "sampleImage";
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        ProductVariation productVariation = new ProductVariation();
        Products product = new Products();
        product.setProductId(productId);
        when(productService.findById(productId)).thenReturn(Optional.of(product));
        when(fileS3Service.uploadFile(thumbnail)).thenThrow(new RuntimeException("File upload error"));
        String result = ProductManagementController.addProductVariation(productId, productVariation, thumbnail, null, null, null, imageName, redirectAttributes, session);
        assertEquals("redirect:/addProductVariationForm/" + productId, result);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("Không thể tải lên hình ảnh. Vui lòng thử lại.", redirectAttributes.getFlashAttributes().get("errorMessage"));
        verify(productService, times(1)).findById(productId);
        verify(fileS3Service, times(1)).uploadFile(thumbnail);
        verify(productService, never()).addProductImage(any());
        verify(productService, never()).addProductVariation(any());
    }

}