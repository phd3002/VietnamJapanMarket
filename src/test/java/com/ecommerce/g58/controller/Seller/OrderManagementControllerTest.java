package com.ecommerce.g58.controller.Seller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.ecommerce.g58.controller.OrderController;
import com.ecommerce.g58.service.OrderDetailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

public class OrderManagementControllerTest {

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    @InjectMocks
    private OrderManagementController orderManagementController;

    // Khai báo trước các giá trị cần thiết
    private Integer orderId = 1;
    private Integer storeId = 1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReturnOrder_UserNotLoggedIn() {
        // Arrange: Principal là null, người dùng chưa đăng nhập
        when(principal.getName()).thenReturn(null);  // Nếu principal là null, người dùng chưa đăng nhập

        // Act: Gọi phương thức returnOrder
        String result = orderManagementController.returnOrder(orderId, storeId, principal, redirectAttributes);

        // Assert: Kiểm tra xem thông báo lỗi đã được thêm vào redirectAttributes chưa
        verify(redirectAttributes).addFlashAttribute("message", "Bạn cần đăng nhập để gửi yêu cầu.");
        verify(redirectAttributes).addFlashAttribute("messageType", "error");

        // Assert: Kiểm tra kết quả trả về
        assertEquals("redirect:/sign-in", result); // Kiểm tra xem kết quả trả về có đúng là "redirect:/sign-in"
    }

    @Test
    public void testReturnOrder_OrderRefundSuccess() {
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
        assertEquals("redirect:order-manager/" + storeId, result);  // Redirect đến order manager
    }

    @Test
    public void testReturnOrder_OrderRefundFailure() {
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
        assertEquals("redirect:order-manager/" + storeId, result);  // Redirect đến order manager
    }
}
