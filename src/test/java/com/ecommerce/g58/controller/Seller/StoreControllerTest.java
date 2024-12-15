package com.ecommerce.g58.controller.Seller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.exception.SpringBootFileUploadException;
import com.ecommerce.g58.service.CountryService;
import com.ecommerce.g58.service.FileS3Service;
import com.ecommerce.g58.service.StoreService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

public class StoreControllerTest {
    @InjectMocks
    private StoreController storeController;

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

    @Mock
    private Stores store;

    @Mock
    private Countries country;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveStoreInfo_tc1() throws Exception {
        Integer storeId = 1;
        String storeName = "samsung";
        String storePhone = "8687462752";
        String storeAddress = "dong anh,ha noi";
        String storeDescription = "Ban dien thoai";
        String storeTown = "Dong Anh";
        String storeCity = "Hà Nội";
        String storeDistrict = "District";
        String postalCode = "136000";
        String storeMail = "lequyet180902@gmail.com";
        Optional<Stores> optionalStore = Optional.of(store);
        Optional<Countries> optionalCountry = Optional.of(country);
        when(storeService.findById(storeId)).thenReturn(optionalStore);
        when(countryService.findById(2)).thenReturn(optionalCountry);
        when(storeImg.isEmpty()).thenReturn(true);
        when(fileS3Service.uploadFile(storeImg)).thenReturn("http://example.com/file.jpg");
        when(store.getPictureUrl()).thenReturn("http://old-image.com");
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(storeService).saveStore(store);
        assertEquals("http://old-image.com", store.getPictureUrl());
        verify(redirectAttributes).addFlashAttribute(eq("message"), eq("Thông tin cửa hàng đã được lưu thành công."));
    }
    @Test
    public void testSaveStoreInfo_tc2() throws Exception {
        Integer storeId = 1;
        String storeName = "A".repeat(255);
        String storePhone = "8687462752";
        String storeAddress = "dong anh,ha noi";
        String storeDescription = "Ban dien thoai";
        String storeTown = "Dong Anh";
        String storeCity = "Hà Nội";
        String storeDistrict = "District";
        String postalCode = "136000";
        String storeMail = "lequyet180902@gmail.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc3() throws Exception {
        Integer storeId = 1;
        String storeName = "";
        String storePhone = "8687462752";
        String storeAddress = "dong anh,ha noi";
        String storeDescription = "Ban dien thoai";
        String storeTown = "Dong Anh";
        String storeCity = "Hà Nội";
        String storeDistrict = "District";
        String postalCode = "136000";
        String storeMail = "lequyet180902@gmail.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Tên cửa hàng không được để trống và không được vượt quá 100 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc4() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "12345";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
    }
    @Test
    public void testSaveStoreInfo_tc5() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1".repeat(21);
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
    }
    @Test
    public void testSaveStoreInfo_tc6() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
    }
    @Test
    public void testSaveStoreInfo_tc7() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "12345111111@";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Số điện thoại không được để trống và không được dưới 10 ký tự hoặc vượt quá 20 ký tự và không chứa ký tự đặc biệt."));
    }
    @Test
    public void testSaveStoreInfo_tc8() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Địa chỉ không được để trống, không được vượt quá 255 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc9() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "a".repeat(256);
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Địa chỉ không được để trống, không được vượt quá 255 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc10() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "a".repeat(101);
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Email cửa hàng không được để trống và không được vượt quá 100 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc11() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Email cửa hàng không được để trống và không được vượt quá 100 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc12() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "a".repeat(501);
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Mô tả cửa hàng không được vượt quá 500 ký tự."));
    }
    @Test
    public void testSaveStoreInfo_tc13() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Xã không được để trống."));
    }
    @Test
    public void testSaveStoreInfo_tc14() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "a".repeat(256);
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Xã không được để trống."));
    }
    @Test
    public void testSaveStoreInfo() throws Exception {
        Integer storeId = 1;
        String storeName = "My Store";
        String storePhone = "1234567890";
        String storeAddress = "123 Main St";
        String storeDescription = "Best store in town";
        String storeTown = "Town";
        String storeCity = "City";
        String storeDistrict = "District";
        String postalCode = "12345";
        String storeMail = "store@example.com";
        when(storeService.findById(storeId)).thenReturn(Optional.empty());
        when(countryService.findById(2)).thenReturn(Optional.of(new Countries()));
        String result = storeController.saveStoreInfo(
                storeId, storeName, storePhone, storeAddress, storeDescription, storeTown,
                storeCity, storeDistrict, postalCode, storeMail, storeImg, redirectAttributes, model
        );
        assertEquals("redirect:/store-info", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Không tìm thấy thông tin cửa hàng hoặc quốc gia."));
    }

}