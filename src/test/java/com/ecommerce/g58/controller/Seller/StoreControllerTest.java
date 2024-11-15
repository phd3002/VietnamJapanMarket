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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

@WebMvcTest(StoreController.class)
@ExtendWith(SpringExtension.class)
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

    @Test
    public void testSaveStoreInfo_Success() throws IOException, SpringBootFileUploadException {
        Integer storeId = 1;
        String storeName = "Test Store";
        String storePhone = "123456789";
        String storeAddress = "Test Address";
        Integer countryId = 1;
        String storeDescription = "Description of the store";
        String storeCity = "Test City";
        String storeDistrict = "Test District";
        String postalCode = "123456";
        String storeMail = "store@example.com";
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




}
