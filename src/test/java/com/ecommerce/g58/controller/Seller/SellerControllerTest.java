package com.ecommerce.g58.controller.Seller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.CountryService;
import com.ecommerce.g58.service.StoreService;
import com.ecommerce.g58.service.UserService;

import java.util.ArrayList;
import java.util.Optional;

public class SellerControllerTest {

    @InjectMocks
    private SellerController sellerController;

    @Mock
    private StoreService storeService;

    @Mock
    private UserService userService;

    @Mock
    private CountryService countryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Khởi tạo các mock
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testRegisterStore_Success() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Countries mockCountry = new Countries();
        mockCountry.setCountryId(1);
        when(countryService.getCountryById(1)).thenReturn(mockCountry);
        Stores store = new Stores();
        store.setStoreName("Test Store");
        store.setStorePhone("123456789");
        store.setStoreAddress("123 Test Address");
        when(storeService.findByStoreName("Test Store")).thenReturn(Optional.empty());
        String result = sellerController.registerStore(store, 1, "Test City", "Test District", "12345", redirectAttributes, null);

        verify(storeService, times(1)).registerStore(store);
        verify(storeService, times(1)).updateUserRoleToSeller(mockEntityUser);
        assertEquals("redirect:/seller/dashboard", result);
    }


    @Test
    public void testRegisterStore_StoreNameExists() {
        // Mock authentication details
        Users mockUser = new Users();
        mockUser.setEmail("test@example.com");

        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        Stores store = new Stores();
        store.setStoreName("Existing Store");

        when(storeService.findByStoreName("Existing Store")).thenReturn(Optional.of(store));

        // Gọi phương thức
        String result = sellerController.registerStore(
                store,
                1,
                "Test City",
                "Test District",
                "12345",
                redirectAttributes,
                null
        );

        // Kiểm tra thông báo lỗi
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên cửa hàng đã tồn tại. Vui lòng chọn tên khác");
        assertEquals("redirect:/sign-up-seller", result);
    }

    @Test
    public void testRegisterStore_MissingStoreName() {
        // Mock authentication details
        Users mockUser = new Users();
        mockUser.setEmail("test@example.com");

        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        Stores store = new Stores(); // Không có tên cửa hàng

        // Gọi phương thức
        String result = sellerController.registerStore(
                store,
                1,
                "Test City",
                "Test District",
                "12345",
                redirectAttributes,
                null
        );

        // Kiểm tra thông báo lỗi
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên cửa hàng không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }
}
