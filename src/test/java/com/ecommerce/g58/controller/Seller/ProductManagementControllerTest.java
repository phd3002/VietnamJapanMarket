package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.repository.ProductVariationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.StoreService;
import javax.servlet.http.HttpSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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
        product.setProductName("Quần".repeat(100));
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

    // testAddProductFull tc3
    @Test
    void testAddProductFull_tc3() throws Exception {
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

    // testAddProductFull tc4
    @Test
    void testAddProductFull_tc4() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống".repeat(500));
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

    // testAddProductFull tc5
    @Test
    void testAddProductFull_tc5() throws Exception {
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

    // testAddProductFull tc6
    @Test
    void testAddProductFull_tc6() throws Exception {
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

    // testAddProductFull tc7
    @Test
    void testAddProductFull_tc7() throws Exception {
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

    // testAddProductFull tc8
    @Test
    void testAddProductFull_tc8() throws Exception {
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

    // testAddProductFull tc9
    @Test
    void testAddProductFull_tc9() throws Exception {
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

    // testAddProductFull tc10
    @Test
    void testAddProductFull_tc10() throws Exception {
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

    // testAddProductFull tc11
    @Test
    void testAddProductFull_tc11() throws Exception {
        Products product = new Products();
        product.setProductName("Quần");
        product.setProductDescription("dài ống");
        product.setPrice(100000);
        product.setWeight(1.0f);
        ProductImage productImage = new ProductImage();
        productImage.setImageName("Ảnh quần".repeat(255));
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

    // testAddProductFull tc12
    @Test
    void testAddProductFull_tc12() throws Exception {
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

    // testAddProductFull tc13
    @Test
    void testAddProductFull_tc13() throws Exception {
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

    // testAddProductFull tc14
    @Test
    void testAddProductFull_tc14() throws Exception {
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
        Integer productId = 1;
        when(productService.findById(productId)).thenReturn(Optional.empty());
        String result = ProductManagementController.updateProduct(productId, "Valid Name", "Valid Description", 50000, 2.5f, redirectAttributes);
        assertEquals("redirect:/edit-product/" + productId, result);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Không tìm thấy sản phẩm.");
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
        Products product = new Products();
        product.setProductId(productId);
        product.setStoreId(new Stores());
        product.getStoreId().setStoreId(1);
        ProductVariation productVariation = new ProductVariation();
        ProductImage productImage = new ProductImage();
        productImage.setImageId(1);
        when(productService.findById(productId)).thenReturn(Optional.of(product));
        when(fileS3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://example.com/image.jpg");
        when(productService.getMaxImageId()).thenReturn(productImage);
        String result = ProductManagementController.addProductVariation(
                productId, productVariation, thumbnail, firstImage, secondImage, thirdImage,
                "Test Image", redirectAttributes, session
        );
        assertEquals("redirect:/seller-products/1", result);
        verify(productService, times(1)).addProductImage(any(ProductImage.class));
        verify(productService, times(1)).addProductVariation(productVariation);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Thêm biến thể sản phẩm thành công!");
    }

    @Test
    void testAddProductVariation_tc2() {
        Integer productId = 1;
        when(productService.findById(productId)).thenReturn(Optional.empty());
        when(session.getAttribute("storeId")).thenReturn(1);
        String result = ProductManagementController.addProductVariation(
                productId, new ProductVariation(), thumbnail, firstImage, secondImage, thirdImage,
                "Test Image", redirectAttributes, session
        );
        assertEquals("redirect:/seller-products/1", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
        verify(productService, never()).addProductImage(any(ProductImage.class));
        verify(productService, never()).addProductVariation(any(ProductVariation.class));
    }

    @Test
    void testAddProductVariation_tc3() throws Exception {
        Integer productId = 1;
        Products product = new Products();
        product.setProductId(productId);
        product.setStoreId(new Stores());
        product.getStoreId().setStoreId(1);
        ProductVariation productVariation = new ProductVariation();
        when(productService.findById(productId)).thenReturn(Optional.of(product));
        when(fileS3Service.uploadFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Upload error"));
        String result = ProductManagementController.addProductVariation(
                productId, productVariation, thumbnail, firstImage, secondImage, thirdImage,
                "Test Image", redirectAttributes, session
        );
        assertEquals("redirect:/addProductVariationForm/1", result);
        verify(redirectAttributes, times(1)).addFlashAttribute(
                "errorMessage", "Không thể tải lên hình ảnh. Vui lòng thử lại.");
        verify(productService, never()).addProductImage(any(ProductImage.class));
        verify(productService, never()).addProductVariation(any(ProductVariation.class));
    }

}