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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


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

    private Map<String, Users> temporaryUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        temporaryUsers = new HashMap<>();
        ReflectionTestUtils.setField(userController, "temporaryUsers", temporaryUsers);
    }
    // testRegisterUser_Success tc1
    @Test
    public void testRegisterUser_Success() throws Exception {
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        Users users = new Users();
        users.setEmail("lequyet180902@gmail.com");
        users.setPassword("123456");
        String confirmPassword = "123456";
        when(userService.isEmailExist("lequyet180902@gmail.com")).thenReturn(false);
        when(request.getSession()).thenReturn(session);
        String result = userController.registerUser(users, confirmPassword, model, mockRedirectAttributes, request);
        assertEquals("sign-up", result);
    }

    // testRegisterUser_PasswordsDoNotMatch tc2
    @Test
    void testRegisterUser_PasswordsDoNotMatchtc2() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123@", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Username không được để trống.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_PasswordsDoNotMatch tc3
    @Test
    void testRegisterUser_PasswordsDoNotMatchtc3() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }



    // testRegisterUser_EmailAlreadyExiststc7
    @Test
    void testRegisterUser_EmailAlreadyExiststc7() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "123456", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Email đã tồn tại.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailAlreadyExiststc8
    @Test
    void testRegisterUser_EmailAlreadyExiststc8() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "123", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailAlreadyExiststc9
    @Test
    void testRegisterUser_EmailAlreadyExiststc9() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailAlreadyExiststc10
    @Test
    void testRegisterUser_EmailNulltc10() throws Exception {
        Users user = new Users();
        user.setEmail("");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123456", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_PasswordsNull tc11
    @Test
    void testRegisterUser_EmailNulltc11() throws Exception {
        Users user = new Users();
        user.setEmail("");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_PasswordsNull tc12
    @Test
    void testRegisterUser_EmailNulltc12() throws Exception {
        Users user = new Users();
        user.setEmail("");
        user.setPassword("123456");
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }


    // testRegisterUser_PasswordNull tc13
    @Test
    public void testRegisterUser_PasswordNulltc13() throws Exception {
        Users users = new Users();
        users.setEmail("lequyet180902@gmail.com");
        users.setPassword("");
        String confirmPassword = "";
        when(userService.isEmailExist("lequyet180902@gmail.com")).thenReturn(false);
        when(request.getSession()).thenReturn(session);
        String result = userController.registerUser(users, confirmPassword, model, redirectAttributes, request);
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_PasswordsDoNotMatch tc14
    @Test
    void testRegisterUser_PasswordsDoNotMatchtc14() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("");
        String result = userController.registerUser(user, "123@", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailAlreadyExiststc17
    @Test
    void testRegisterUser_EmailAlreadyExiststc17() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Email đã tồn tại.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailAlreadyExiststc18
    @Test
    void testRegisterUser_EmailAlreadyExiststc18() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("");
        when(userService.isEmailExist(user.getEmail())).thenReturn(true);
        String result = userController.registerUser(user, "123", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_EmailNulltc19
    @Test
    void testRegisterUser_EmailNulltc19() throws Exception {
        Users user = new Users();
        user.setEmail("");
        user.setPassword("");
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_EmailNull tc20
    @Test
    void testRegisterUser_EmailNulltc20() throws Exception {
        Users user = new Users();
        user.setEmail("");
        user.setPassword("");
        String result = userController.registerUser(user, "123", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }



//---------------------------------------------------------------------------------------------------------------------
    // testLoginSubmit tc1
    @Test
    public void testLoginSubmit_tc1() {
    when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
    UserDetails userDetails = mock(UserDetails.class);
    when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
    when(userDetails.getPassword()).thenReturn("123456");
    when(userService.checkPassword("password", "123456")).thenReturn(true);
    String result = userController.loginSubmit("lequyet180902@gmail.com", "password", model);
    assertEquals("redirect:/homepage", result);
    verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
    verify(userService, times(1)).checkPassword("password", "123456");
}

    // testLoginSubmit tc2
    @Test
    public void testLoginSubmit_tc2() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("Password", "")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
        verify(userService, times(1)).checkPassword("Password", "");
    }

    // testLoginSubmit tc3
    @Test
    public void testLoginSubmit_tc3() {
        // Mock dependencies
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("wrongPassword", "123456")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "wrongPassword", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
        verify(userService, times(1)).checkPassword("wrongPassword", "123456");
    }

    // testLoginSubmit tc4
    @Test
    public void testLoginSubmit_tc4() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(null);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "123456", model);
        assertEquals("sign-in", result); //
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
    }

    // testLoginSubmit tc5
    @Test
    public void testLoginSubmit_tc5() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(null);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
    }

    // testLoginSubmit_tc6
    @Test
    public void testLoginSubmit_tc6() {
        // Mock dependencies
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(null);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "123@!1", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
    }

    // testLoginSubmit tc7
    @Test
    public void testLoginSubmit_tc7() {
        when(userService.isAccountActive("")).thenReturn(true);
        when(userService.loadUserByUsername("")).thenReturn(null);
        String result = userController.loginSubmit("", "123456", model);
        assertEquals("sign-in", result); //
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("");
        verify(userService, times(1)).loadUserByUsername("");
    }

    // testLoginSubmit tc8
    @Test
    public void testLoginSubmit_tc8() {
        when(userService.isAccountActive("")).thenReturn(true);
        when(userService.loadUserByUsername("")).thenReturn(null);
        String result = userController.loginSubmit("", "", model);
        assertEquals("sign-in", result); //
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("");
        verify(userService, times(1)).loadUserByUsername("");
    }

    // testLoginSubmit_tc9
    @Test
    public void testLoginSubmit_tc9() {
        when(userService.isAccountActive("")).thenReturn(true);
        when(userService.loadUserByUsername("")).thenReturn(null);
        String result = userController.loginSubmit("", "1123!@1", model);
        assertEquals("sign-in", result); //
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email chưa được đăng kí"));
        verify(userService, times(1)).isAccountActive("");
        verify(userService, times(1)).loadUserByUsername("");
    }

    // testLoginSubmit_tc10
    @Test
    public void testLoginSubmit_tc10() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "123456", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Tài khoản của bạn đã bị khóa."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    }

    // testLoginSubmit_tc11
    @Test
    public void testLoginSubmit_tc11() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenThrow(new BadCredentialsException("Invalid credentials"));
        String result = userController.loginSubmit("lequyet180902@gmail.com", "123456", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    }

    // testLoginSubmit_tc12
    @Test
    public void testLoginSubmit_tc12() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenThrow(new RuntimeException("Unexpected error"));
        String result = userController.loginSubmit("lequyet180902@gmail.com", "123456", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    }


    //---------------------------------------------------------------------------------------------------------------------
    // testProcessForgotPassword tc1
    @Test
    public void testForgotPassword_tc1() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(true);
        String result = userController.processForgotPassword("lequyet180902@gmail.com", redirectAttributes, request, model);
        assertEquals("redirect:/forgot-password", result);
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
        verify(userService, times(1)).sendResetPasswordEmail(eq("lequyet180902@gmail.com"), eq(request));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), eq("Chúng tôi đã gửi Email về cho bạn"));
    }

    // testProcessForgotPassword tc2
    @Test
    public void testForgotPassword_tc2() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenThrow(new BadCredentialsException("User not found"));
        String result = userController.processForgotPassword("lequyet180902@gmail.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    }

    // testProcessForgotPassword tc3
    @Test
    public void testForgotPassword_tc3() {
        when(userService.isAccountActive("")).thenThrow(new BadCredentialsException("User not found"));
        String result = userController.processForgotPassword("", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
        verify(userService, times(1)).isAccountActive("");
    }

    // testProcessForgotPassword tc4
    @Test
    public void testForgotPassword_tc4() {
        when(userService.isAccountActive("lequyet180902@gmail.com")).thenReturn(false);
        String result = userController.processForgotPassword("lequyet180902@gmail.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Tài khoản của bạn đã bị khóa và không thể thực hiện yêu cầu đặt lại mật khẩu."));
        verify(userService, times(1)).isAccountActive("lequyet180902@gmail.com");
    }


    //----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testProcessResetPassword_ShortPassword() {
        String result = userController.processResetPassword("validToken", "12345", "12345", redirectAttributes, model);
        assertEquals("/reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mật Khẩu phải dài ít nhất 6 ký tự.");
    }

    @Test
    public void testProcessResetPassword_PasswordsDoNotMatch() {
        String result = userController.processResetPassword("validToken", "password123", "password456", redirectAttributes, model);
        assertEquals("/reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mật Khẩu và Xác Nhận Mật Khẩu không khớp.");
    }

    @Test
    public void testProcessResetPassword_PasswordsDoNotMatch1() {
        String result = userController.processResetPassword("validToken", "pas123213213", "password456", redirectAttributes, model);
        assertEquals("/reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mật Khẩu và Xác Nhận Mật Khẩu không khớp.");
    }


    @Test
    public void testProcessResetPassword_Success() {
        doNothing().when(userService).updatePasswordReset(anyString(), anyString());
        String result = userController.processResetPassword("validToken", "password123", "password123", redirectAttributes, model);
        assertEquals("redirect:/sign-in", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Đặt lại Mật Khẩu thành công.");
    }


    @Test
    public void testProcessResetPassword_Error() {
        doThrow(new RuntimeException("Some error")).when(userService).updatePasswordReset(anyString(), anyString());
        String result = userController.processResetPassword("validToken", "password123", "password123", redirectAttributes, model);
        assertEquals("redirect:/reset-password?token=validToken", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("errorMessage", "Error: Some error");
    }


    //-----------------------------------------------------------------------------------------------------
    // testShowVerifyOtpForm_Success tc1
    @Test
    public void testProcessVerifyOtp_Success() throws Exception {
        String otp = "123456";
        Users user = new Users();
        temporaryUsers.put(otp, user);
        when(session.getAttribute("otp")).thenReturn(otp);
        when(request.getParameter("otp")).thenReturn(otp);
        String result = userController.processVerifyOtp(request, session, redirectAttributes);
        assertEquals("/sign-in", result);
        verify(userService).registerUser(user);
        verify(session).setAttribute("verificationSuccessMessage", "Xác minh OTP thành công!");
        verify(session).removeAttribute("otp");
        assertFalse(temporaryUsers.containsKey(otp));
    }

    // testProcessVerifyOtp_MistakeOtp tc2
    @Test
    public void testProcessVerifyOtp_MistakeOtp() throws Exception {
        String otp = "123456";
        String invalidOtp = "654321";
        when(session.getAttribute("otp")).thenReturn(otp);
        when(request.getParameter("otp")).thenReturn(invalidOtp);
        String result = userController.processVerifyOtp(request, session, redirectAttributes);
        assertEquals("redirect:/sign-up/confirm-code", result);
        verify(redirectAttributes).addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!");
        verify(userService, never()).registerUser(any(Users.class));
    }

    // testProcessVerifyOtp_OtpNull tc3
    @Test
    public void testProcessVerifyOtp_OtpNull() throws Exception {
        String otp = "123456";
        String invalidOtp = "";
        when(session.getAttribute("otp")).thenReturn(otp);
        when(request.getParameter("otp")).thenReturn(invalidOtp);
        String result = userController.processVerifyOtp(request, session, redirectAttributes);
        assertEquals("redirect:/sign-up/confirm-code", result);
        verify(redirectAttributes).addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!");
        verify(userService, never()).registerUser(any(Users.class));
    }

    // testProcessVerifyOtp_OtpSpepcialchar tc4
    @Test
    public void testProcessVerifyOtp_OtpSpepcialchar() throws Exception {
        String otp = "123456";
        String invalidOtp = "!@@131";
        when(session.getAttribute("otp")).thenReturn(otp);
        when(request.getParameter("otp")).thenReturn(invalidOtp);
        String result = userController.processVerifyOtp(request, session, redirectAttributes);
        assertEquals("redirect:/sign-up/confirm-code", result);
        verify(redirectAttributes).addFlashAttribute("error", "OTP không hợp lệ. Vui lòng thử lại!");
        verify(userService, never()).registerUser(any(Users.class));
    }



}
