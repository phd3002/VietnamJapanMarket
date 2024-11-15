package com.ecommerce.g58.controller.Seller;

import static org.mockito.Mockito.*;
import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.exception.SpringBootFileUploadException;
import com.ecommerce.g58.service.CountryService;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private CountryService countryService;

    @Mock
    private FileS3Service fileS3Service;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Model model;

    @Mock
    private MultipartFile storeImg;

    @InjectMocks
    private StoreController storeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Test testSaveStoreInfo tc1
    @Test
    public void testSaveStoreInfo_tc1() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "0868746275";
        String storeAddress = "Đông Anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(storeService).saveStore(any(Stores.class));
        verify(redirectAttributes).addFlashAttribute(eq("message"), eq("Thông tin cửa hàng đã được lưu thành công."));
    }

    // Test testSaveStoreInfo tc2
    @Test
    public void testSaveStoreInfo_tc2() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Quốc gia không được để trống."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc3
    @Test
    public void testSaveStoreInfo_tc3() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc4
    @Test
    public void testSaveStoreInfo_tc4() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc5
    @Test
    public void testSaveStoreInfo_tc5() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "0868746275";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc6
    @Test
    public void testSaveStoreInfo_tc6() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "0868746275";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo_ tc7
    @Test
    public void testSaveStoreInfo_tc7() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "11111";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc8
    @Test
    public void testSaveStoreInfo_tc8() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "11111";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc9
    @Test
    public void testSaveStoreInfo_tc9() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "1111111111111122222222222222222222222";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc10
    @Test
    public void testSaveStoreInfo_tc10() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "1111111111111122222222222222222222222";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc11
    @Test
    public void testSaveStoreInfo_tc11() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc12
    @Test
    public void testSaveStoreInfo_tc12() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc13
    @Test
    public void testSaveStoreInfo_tc13() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "1112222@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc14
    @Test
    public void testSaveStoreInfo_tc14() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSung";
        String storePhone = "1112222@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc15
    @Test
    public void testSaveStoreInfo_tc15() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc16
    @Test
    public void testSaveStoreInfo_tc16() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc15
    @Test
    public void testSaveStoreInfo_tc17() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "1111111111111111111111111111111111111111";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc18
    @Test
    public void testSaveStoreInfo_tc18() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "1111111111111111111111111111111111111111";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc19
    @Test
    public void testSaveStoreInfo_tc19() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc20
    @Test
    public void testSaveStoreInfo_tc20() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc21
    @Test
    public void testSaveStoreInfo_tc21() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "11111!@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc22
    @Test
    public void testSaveStoreInfo_tc22() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamSungssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "11111!@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc23
    @Test
    public void testSaveStoreInfo_tc23() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc24
    @Test
    public void testSaveStoreInfo_tc24() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc25
    @Test
    public void testSaveStoreInfo_tc25() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "1111111111111111111111111111111111111111";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc26
    @Test
    public void testSaveStoreInfo_tc26() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "1111111111111111111111111111111111111111";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc27
    @Test
    public void testSaveStoreInfo_tc27() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc28
    @Test
    public void testSaveStoreInfo_tc28() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = null;
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc29
    @Test
    public void testSaveStoreInfo_tc29() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "11111!@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc30
    @Test
    public void testSaveStoreInfo_tc30() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "11111!@@@@";
        String storeAddress = "Đông anh";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc31
    @Test
    public void testSaveStoreInfo_tc31() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Địa chỉ không được để trống và không được vượt quá 255 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc32
    @Test
    public void testSaveStoreInfo_tc32() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Địa chỉ không được để trống và không được vượt quá 255 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc33
    @Test
    public void testSaveStoreInfo_tc33() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc34
    @Test
    public void testSaveStoreInfo_tc34() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc35
    @Test
    public void testSaveStoreInfo_tc35() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc36
    @Test
    public void testSaveStoreInfo_tc36() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc37
    @Test
    public void testSaveStoreInfo_tc37() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc38
    @Test
    public void testSaveStoreInfo_tc38() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc39
    @Test
    public void testSaveStoreInfo_tc39() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc40
    @Test
    public void testSaveStoreInfo_tc40() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Samsung";
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc41
    @Test
    public void testSaveStoreInfo_tc41() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc42
    @Test
    public void testSaveStoreInfo_tc42() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc43
    @Test
    public void testSaveStoreInfo_tc43() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc44
    @Test
    public void testSaveStoreInfo_tc44() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc45
    @Test
    public void testSaveStoreInfo_tc45() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc46
    @Test
    public void testSaveStoreInfo_tc46() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc47
    @Test
    public void testSaveStoreInfo_tc47() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc48
    @Test
    public void testSaveStoreInfo_tc48() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc49
    @Test
    public void testSaveStoreInfo_tc49() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc50
    @Test
    public void testSaveStoreInfo_tc50() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "SamsungSssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc51
    @Test
    public void testSaveStoreInfo_tc51() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc52
    @Test
    public void testSaveStoreInfo_tc52() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "0868746275";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc53
    @Test
    public void testSaveStoreInfo_tc53() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc54
    @Test
    public void testSaveStoreInfo_tc54() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc55
    @Test
    public void testSaveStoreInfo_tc55() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc56
    @Test
    public void testSaveStoreInfo_tc56() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "111111222222222222222222222";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc57
    @Test
    public void testSaveStoreInfo_tc57() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc48
    @Test
    public void testSaveStoreInfo_tc58() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = null;
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc59
    @Test
    public void testSaveStoreInfo_tc59() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = 1;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }

    // Test testSaveStoreInfo tc60
    @Test
    public void testSaveStoreInfo_tc60() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = null;
        String storePhone = "1111111!@!@";
        String storeAddress = "Đông anh sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Integer countryId = null;
        String storeDescription = "Dep qua";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Da";
        String postalCode = "123456";
        String storeMail = "lequyet180902@gmail.com";
        String storeImgUrl = "http://example.com/store-image.jpg";
        Stores store = new Stores();
        store.setStoreId(storeId);
        Countries country = new Countries();
        country.setCountryId(countryId);
        when(storeService.findById(storeId)).thenReturn(Optional.of(store));
        when(countryService.findById(countryId)).thenReturn(Optional.of(country));
        when(fileS3Service.uploadFile(storeImg)).thenReturn(storeImgUrl);
        when(storeImg.isEmpty()).thenReturn(false);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, countryId,
                storeDescription, storeCity, storeDistrict, postalCode,
                storeMail, storeImg, redirectAttributes, model);
        assertEquals("redirect:/store-info/" + storeId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
        verify(storeService, never()).saveStore(any(Stores.class));
    }


}
