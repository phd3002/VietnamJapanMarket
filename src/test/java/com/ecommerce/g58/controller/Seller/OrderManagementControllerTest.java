package com.ecommerce.g58.controller.Seller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.OrderDetailService;
import com.ecommerce.g58.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

public class OrderManagementControllerTest {

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    @InjectMocks
    private OrderManagementController orderManagementController;

    private Integer orderId = 1;
    private Integer storeId = 1;

    @Mock
    private OrderService orderService;


    @Mock
    private Authentication authentication;
    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReturnOrder_tc1() {
        // Arrange: Principal là null, người dùng chưa đăng nhập
        when(principal.getName()).thenReturn(null);  // Nếu principal là null, người dùng chưa đăng nhập

        // Act: Gọi phương thức returnOrder
        String result = orderManagementController.returnOrder(orderId, storeId, principal, redirectAttributes);

        // Assert: Kiểm tra xem thông báo lỗi đã được thêm vào redirectAttributes chưa
        verify(redirectAttributes).addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
        verify(redirectAttributes).addFlashAttribute("messageType", "error");

        // Assert: Kiểm tra kết quả trả về
        assertEquals("redirect:order-manager/", result); // Kiểm tra xem kết quả trả về có đúng là "redirect:/sign-in"
    }

    @Test
    public void testReturnOrder_tc2() {
        // Arrange: Người dùng đã đăng nhập
        when(principal.getName()).thenReturn("testuser");

        // Giả lập hành vi của service trả về true (hoàn trả đơn hàng thành công)
        when(orderDetailService.refundOrder(orderId)).thenReturn(true);

        // Act: Gọi phương thức returnOrder
        String result = orderManagementController.returnOrder(orderId, storeId, principal, redirectAttributes);

        // Assert: Kiểm tra xem thông báo thành công đã được thêm vào redirectAttributes chưa
        verify(redirectAttributes).addFlashAttribute("message", "Hoàn trả đơn hàng thành công.");
        verify(redirectAttributes).addFlashAttribute("messageType", "success");

        // Assert: Kiểm tra kết quả trả về
        assertEquals("redirect:order-manager/" , result);  // Redirect đến order manager
    }

    @Test
    public void testReturnOrder_tc3() {
        // Arrange: Người dùng đã đăng nhập
        when(principal.getName()).thenReturn("testuser");

        // Giả lập hành vi của service trả về false (không thể hoàn trả đơn hàng)
        when(orderDetailService.refundOrder(orderId)).thenReturn(false);

        // Act: Gọi phương thức returnOrder
        String result = orderManagementController.returnOrder(orderId, storeId, principal, redirectAttributes);

        // Assert: Kiểm tra xem thông báo lỗi đã được thêm vào redirectAttributes chưa
        verify(redirectAttributes).addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
        verify(redirectAttributes).addFlashAttribute("messageType", "error");

        // Assert: Kiểm tra kết quả trả về
        assertEquals("redirect:order-manager/" , result);  // Redirect đến order manager
    }

    @Test
    public void testBulkUpdateOrderStatus_tc1() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        String statusFilter = "PENDING";
        String newStatus = "SHIPPED";
        String startDateStr = "2025-01-01";
        String endDateStr = "2025-01-31";
        Integer storeId = 1;
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        Stores store = new Stores();
        store.setStoreId(storeId);
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(storeRepository.findByOwnerId(user)).thenReturn(Optional.of(store));
        String result = orderManagementController.bulkUpdateOrderStatus(
                statusFilter, startDateStr, endDateStr, newStatus, redirectAttributes, principal
        );
        assertEquals("redirect:/seller/order-manager?status=PENDING&startDate=2025-01-01&endDate=2025-01-31", result);
        verify(orderService, times(1)).bulkUpdateOrderStatus(
                statusFilter, newStatus, LocalDate.parse(startDateStr), LocalDate.parse(endDateStr), storeId);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Đã cập nhật trạng thái các đơn hàng thành công.");
        verify(redirectAttributes, times(1)).addFlashAttribute("messageType", "success");
    }

    @Test
    public void testBulkUpdateOrderStatus_tc2() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        String statusFilter = "PENDING";
        String newStatus = "SHIPPED";
        String startDateStr = "2025-01-01";
        String endDateStr = "2025-01-31";
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(storeRepository.findByOwnerId(user)).thenReturn(Optional.empty());
        String result = orderManagementController.bulkUpdateOrderStatus(
                statusFilter, startDateStr, endDateStr, newStatus, redirectAttributes, principal
        );
        assertEquals("redirect:/seller/order-manager", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Không tìm thấy cửa hàng của bạn.");
        verify(redirectAttributes, times(1)).addFlashAttribute("messageType", "error");
        verify(orderService, never()).bulkUpdateOrderStatus(anyString(), anyString(), any(), any(), anyInt());
    }

    @Test
    public void testBulkUpdateOrderStatus_tc3() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        String statusFilter = "PENDING";
        String newStatus = "SHIPPED";
        String startDateStr = "20250131";
        String endDateStr = "2025-01-31";
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        Stores store = new Stores();
        store.setStoreId(1);
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        when(storeRepository.findByOwnerId(user)).thenReturn(Optional.of(store));
        String result = orderManagementController.bulkUpdateOrderStatus(
                statusFilter, startDateStr, endDateStr, newStatus, redirectAttributes, principal
        );
        assertEquals("redirect:/seller/order-manager", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Định dạng ngày không hợp lệ.");
        verify(redirectAttributes, times(1)).addFlashAttribute("messageType", "error");
        verify(orderService, never()).bulkUpdateOrderStatus(anyString(), anyString(), any(), any(), anyInt());
    }

    @Test
    public void testBulkUpdateOrderStatus_tc4() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("lequyet180902@gmail.com");
        String statusFilter = "PENDING";
        String newStatus = "SHIPPED";
        String startDateStr = "2024-01-01";
        String endDateStr = "2024-01-31";
        Integer storeId = 1;
        Users owner = new Users();
        owner.setEmail("lequyet180902@gmail.com");
        Stores store = new Stores();
        store.setStoreId(storeId);
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(owner);
        when(storeRepository.findByOwnerId(owner)).thenReturn(Optional.of(store));
        doThrow(new RuntimeException("Database error")).when(orderService).bulkUpdateOrderStatus(
                statusFilter, newStatus, LocalDate.parse(startDateStr), LocalDate.parse(endDateStr), storeId);
        String result = orderManagementController.bulkUpdateOrderStatus(
                statusFilter, startDateStr, endDateStr, newStatus, redirectAttributes, principal
        );
        assertEquals("redirect:/seller/order-manager?status=PENDING&startDate=2024-01-01&endDate=2024-01-31", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng.");
        verify(redirectAttributes, times(1)).addFlashAttribute("messageType", "error");
        verify(orderService, times(1)).bulkUpdateOrderStatus(
                statusFilter, newStatus, LocalDate.parse(startDateStr), LocalDate.parse(endDateStr), storeId);
    }

    @Test
    public void testBulkUpdateOrderStatus() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(false);
        String result = orderManagementController.bulkUpdateOrderStatus(
                "PENDING", "2024-01-01", "2024-01-31", "SHIPPED", redirectAttributes, principal
        );
        assertEquals("redirect:/login", result);
        verify(orderService, never()).bulkUpdateOrderStatus(anyString(), anyString(), any(), any(), anyInt());
    }
}
