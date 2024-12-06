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

    // test registerStore tc1
    @Test
    public void testRegisterStoretc1() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Countries mockCountry = new Countries();
        mockCountry.setCountryId(1);
        when(countryService.getCountryById(1)).thenReturn(mockCountry);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("0868746275");
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        when(storeService.findByStoreName("Test Store")).thenReturn(Optional.empty());
        String result = sellerController.registerStore(store,
                store.getStoreAddress(),storeTown,
                storeCity,
                storeDistrict,
                postalCode, redirectAttributes, null);
        verify(storeService, times(1)).registerStore(store);
        verify(storeService, times(1)).updateUserRoleToSeller(mockEntityUser);
        assertEquals("redirect:/seller/dashboard", result);
    }

    // test registerStore tc2
    @Test
    public void testRegisterStore_tc2() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store".repeat(100));
        store.setStorePhone("0868746275");
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );

        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên cửa hàng không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc3
    @Test
    public void testRegisterStore_tc3() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("");
        store.setStorePhone("0868746275");
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Tên cửa hàng không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc4
    @Test
    public void testRegisterStore_tc4() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("123@@@@@@@@@@@@");
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Số điện thoại cửa hàng không được để trống ,không được vượt quá 20 ký tự và không có kí tự đặc biệt.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc5
    @Test
    public void testRegisterStore_tc5() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("123".repeat(100));
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Số điện thoại cửa hàng không được để trống ,không được vượt quá 20 ký tự và không có kí tự đặc biệt.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc6
    @Test
    public void testRegisterStore_tc6() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("");
        store.setStoreAddress("Thi tran Dong Anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Số điện thoại cửa hàng không được để trống ,không được vượt quá 20 ký tự và không có kí tự đặc biệt.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc7
    @Test
    public void testRegisterStore_tc7() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("Thi tran Dong Anh".repeat(300));
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Địa chỉ cửa hàng không được để trống và không được vượt quá 255 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc8
    @Test
    public void testRegisterStore_tc8() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Địa chỉ cửa hàng không được để trống và không được vượt quá 255 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc9
    @Test
    public void testRegisterStore_tc9() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("thi tran dong anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi".repeat(100);
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Thành phố không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc10
    @Test
    public void testRegisterStore_tc10() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("thi tran dong anh");
        String storeTown = "Dong Anh";
        String storeCity = "";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Thành phố không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc11
    @Test
    public void testRegisterStore_tc11() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("thi tran dong anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh".repeat(100);
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Quận/huyện không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc12
    @Test
    public void testRegisterStore_tc12() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("thi tran dong anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "";
        String postalCode = "12345";
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Quận/huyện không được để trống và không được vượt quá 100 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc13
    @Test
    public void testRegisterStore_tc13() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        Stores store = new Stores();
        store.setStoreName("FPT Store");
        store.setStorePhone("868746275");
        store.setStoreAddress("thi tran dong anh");
        String storeTown = "Dong Anh";
        String storeCity = "Ha Noi";
        String storeDistrict = "Dong Anh";
        String postalCode = "12345".repeat(100);
        String result = sellerController.registerStore(
                store,
                store.getStoreAddress(),
                storeTown,
                storeCity,
                storeDistrict,
                postalCode,
                redirectAttributes,
                null
        );
        verify(redirectAttributes, times(1))
                .addFlashAttribute("errorMessage", "Mã bưu chính không được vượt quá 20 ký tự.");
        assertEquals("redirect:/sign-up-seller", result);
    }

    // test registerStore tc14
    @Test
    public void testRegisterStore_tc14() {
        User mockUser = new User("leuquyet180902@gmail.com", "123456", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Users mockEntityUser = new Users();
        mockEntityUser.setEmail("leuquyet180902@gmail.com");
        when(userService.findByEmail("leuquyet180902@gmail.com")).thenReturn(mockEntityUser);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        Stores stores = new Stores();
        stores.setStoreName("Valid Store");
        stores.setStorePhone("1234567890");
        stores.setStoreAddress("123 Test Street");
        when(storeService.findByStoreName("Valid Store")).thenReturn(Optional.of(new Stores()));
        Optional<Stores> existingStore = Optional.of(new Stores());
        when(storeService.findByStoreName("Valid Store")).thenReturn(existingStore);
        String result = sellerController.registerStore(stores, stores.getStoreAddress(),"Test Town", "Test City", "Test District", "12345", redirectAttributes, null);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Tên cửa hàng đã tồn tại. Vui lòng chọn tên khác");
        assertEquals("redirect:/sign-up-seller", result);
    }



}
