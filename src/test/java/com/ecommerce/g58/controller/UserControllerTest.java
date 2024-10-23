package com.ecommerce.g58.controller;


import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Dang ki tai khoan nguoi dung sai confirm password.
    @Test
    void testRegisterUser_PasswordsDoNotMatch() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123@", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // Dang ki tai khoan nguoi dung password = null.
    @Test
    void testRegisterUser_PasswordsNull() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("");
        String result = userController.registerUser(user, "123@", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // Dang ki tai khoan nguoi dung email trung.
    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "123456", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Email đã tồn tại.");
        assertEquals("sign-up", result);
    }

    // Dang ki tai khoan nguoi dung thanh cong.
    @Test
    public void testRegisterUser_Success() throws Exception {
        Users users = new Users();
        users.setEmail("lequyet180902@gmail.com");
        users.setPassword("123456");
        when(userService.isEmailExist("lequyet180902@gmail.com")).thenReturn(false);
        when(request.getSession()).thenReturn(session);  // Mock session từ request
        String result = userController.registerUser(users, "123456", model, redirectAttributes, request);
        assertEquals("redirect:/sign-up", result);
        verify(session, times(1)).setAttribute(eq("otp"), anyString());
    }

    // Dang ki tai khoan nguoi dung loi.
    @Test
    void testRegisterUser_ExceptionHandling() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("password");
        when(userService.isEmailExist(user.getEmail())).thenThrow(new RuntimeException("error"));
        String result = userController.registerUser(user, "password", model, redirectAttributes, request);
        verify(redirectAttributes).addFlashAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
        assertEquals("redirect:/sign-up", result);
    }
//---------------------------------------------------------------------------------------------------------------------
    // Test  email không tồn tại
    @Test
    public void testLoginSubmit_EmailNotExists() {
        when(userService.loadUserByUsername("nonexistent@example.com")).thenReturn(null);
        String result = userController.loginSubmit("nonexistent@example.com", "password123", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // Test email null
    @Test
    public void testLoginSubmit_EmailNull() {
        when(userService.loadUserByUsername("")).thenReturn(null);
        String result = userController.loginSubmit("", "password123", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // Test mật khẩu không đúng
    @Test
    public void testLoginSubmit_InvalidPassword() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("wrongPassword", "123")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "wrongPassword", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
    }

    // Test mật khẩu null
    @Test
    public void testLoginSubmit_PasswordNull() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("wrongPassword", "")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "wrongPassword", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
    }

    // Test đăng nhập thành công
    @Test
    public void testLoginSubmit_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("password", "123456")).thenReturn(true);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "password", model);
        assertEquals("redirect:/homepage", result);
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
    }

    //---------------------------------------------------------------------------------------------------------------------
    // Test trường hợp gửi email thành công
    @Test
    public void testProcessForgotPassword_Success() {
        doNothing().when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("lequyet180902@gmail.com", redirectAttributes, request, model);
        assertEquals("redirect:/forgot-password", result);

        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), eq("Chúng tôi đã gửi Email về cho bạn"));
    }

    // Test trường hợp email không tồn tại
    @Test
    public void testProcessForgotPassword_BadCredentials() {
        doThrow(new BadCredentialsException("Email không tồn tại")).when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("nonexistent@gmail.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
    }

    // Test trường hợp xảy ra lỗi khác
    @Test
    public void testProcessForgotPassword_Exception() {
        doThrow(new RuntimeException("Lỗi không xác định")).when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("error@example.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng"));
    }
//----------------------------------------------------------------------------------------------------------------------
    // Test trường hợp token hợp lệ
    @Test
    public void testShowResetPasswordForm_ValidToken() {
        when(userService.isResetTokenValid("validToken")).thenReturn(true);
        String result = userController.showResetPasswordForm("validToken", model);
        assertEquals("reset-password", result);
        verify(model, times(1)).addAttribute("token", "validToken");
    }

    // Test trường hợp token không hợp lệ
    @Test
    public void testShowResetPasswordForm_InvalidToken() {
        when(userService.isResetTokenValid("invalidToken")).thenReturn(false);
        String result = userController.showResetPasswordForm("invalidToken", model);
        assertEquals("reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mã thông báo đặt lại không hợp lệ hoặc đã hết hạn.");
    }
    //----------------------------------------------------------------------------------------------------------------------

    // Test trường hợp mật khẩu ngắn hơn 6 ký tự
    @Test
    public void testProcessResetPassword_ShortPassword() {
        String result = userController.processResetPassword("validToken", "123", "123", redirectAttributes, model);
        assertEquals("/reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mật Khẩu phải dài ít nhất 6 ký tự.");
    }

    // Test trường hợp mật khẩu và xác nhận mật khẩu không khớp
    @Test
    public void testProcessResetPassword_PasswordsDoNotMatch() {
        String result = userController.processResetPassword("validToken", "password123", "password456", redirectAttributes, model);
        assertEquals("/reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mật Khẩu và Xác Nhận Mật Khẩu không khớp.");
    }

    // Test trường hợp đặt lại mật khẩu thành công
    @Test
    public void testProcessResetPassword_Success() {
        doNothing().when(userService).updatePasswordReset(anyString(), anyString());
        String result = userController.processResetPassword("validToken", "password123", "password123", redirectAttributes, model);
        assertEquals("redirect:/sign-in", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Đặt lại Mật Khẩu thành công.");
    }

    // Test trường hợp xảy ra lỗi khi đặt lại mật khẩu
    @Test
    public void testProcessResetPassword_Error() {
        doThrow(new RuntimeException("Some error")).when(userService).updatePasswordReset(anyString(), anyString());
        String result = userController.processResetPassword("validToken", "password123", "password123", redirectAttributes, model);
        assertEquals("redirect:/reset-password?token=validToken", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Error: Some error");
    }
    //---------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testShowForgotPasswordForm() {
        String result = userController.showForgotPasswordForm();
        assertEquals("forgot-password", result);
    }

    //----------------------------------------------------------------------------------------------------------------------
    @Test
    public void testShowRegistrationForm() {
        String viewName = userController.showRegistrationForm(model);
        verify(model, times(1)).addAttribute(eq("users"), any(Users.class));
        assertEquals("sign-up", viewName);
    }

//-----------------------------------------------------------------------------------------------------
    


}
