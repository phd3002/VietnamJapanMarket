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
        Users users = new Users();
        users.setEmail("lequyet180902@gmail.com");
        users.setPassword("123456");
        String confirmPassword = "123456";
        when(userService.isEmailExist("lequyet180902@gmail.com")).thenReturn(false);
        when(request.getSession()).thenReturn(session);
        String result = userController.registerUser(users, confirmPassword, model, redirectAttributes, request);
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_PasswordsDoNotMatch tc2
    @Test
    void testRegisterUser_PasswordsDoNotMatchtc2() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123@", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
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

    // testRegisterUser_Spepcialcharacter tc4
    @Test
    void testRegisterUser_Spepcialcharactertc4() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902#@@!gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123456", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_Spepcialcharacter tc5
    @Test
    void testRegisterUser_Spepcialcharactertc5() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902#@@!gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "123", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("sign-up", result);
    }

    // testRegisterUser_Spepcialcharacter tc6
    @Test
    void testRegisterUser_Spepcialcharactertc6() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902#@@!gmail.com");
        user.setPassword("123456");
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
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

    // testRegisterUser_PasswordsDoNotMatch tc15
    @Test
    void testRegisterUser_PasswordsDoNotMatchtc15() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902#@@!gmail.com");
        user.setPassword("");
        String result = userController.registerUser(user, "", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
    }

    // testRegisterUser_Spepcialcharacter tc16
    @Test
    void testRegisterUser_Spepcialcharactertc16() throws Exception {
        Users user = new Users();
        user.setEmail("lequyet180902#@@!gmail.com");
        user.setPassword("");
        String result = userController.registerUser(user, "1236", model, redirectAttributes, request);
        verify(model).addAttribute("errorMessage", "");
        assertEquals("redirect:/sign-up", result);
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
    // testLoginSubmit_Success tc1
    @Test
    public void testLoginSubmit_Successtc1() {
    UserDetails userDetails = mock(UserDetails.class);
    when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
    when(userDetails.getPassword()).thenReturn("123456");
    when(userService.checkPassword("password", "123456")).thenReturn(true);
    String result = userController.loginSubmit("lequyet180902@gmail.com", "password", model);
    assertEquals("redirect:/homepage", result);
    verify(userService, times(1)).loadUserByUsername("lequyet180902@gmail.com");
}

    // testLoginSubmit_PasswordNulltc2
    @Test
    public void testLoginSubmit_PasswordNulltc2() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("Password", "")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
    }

    // testLoginSubmit_MistakePasswordtc3
    @Test
    public void testLoginSubmit_MistakePasswordtc3() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@gmail.com")).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("wrongPassword", "123")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@gmail.com", "wrongPassword", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Sai tài khoản hoặc mật khẩu."));
    }

    // testLoginSubmit_MistakeUsernametc4
    @Test
    public void testLoginSubmit_MistakeUsernametc4() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet1@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "123456")).thenReturn(false);
        String result = userController.loginSubmit("lequyet1@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_MistakeUsernametc5
    @Test
    public void testLoginSubmit_MistakeUsernametc5() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet1@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("Password", "")).thenReturn(false);
        String result = userController.loginSubmit("lequyet1@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }
    // testLoginSubmit_MistakeUsernametc6
    @Test
    public void testLoginSubmit_MistakeUsernametc6() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet1@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "456")).thenReturn(false);
        String result = userController.loginSubmit("lequyet1@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_Spepcialchar tc7
    @Test
    public void testLoginSubmit_Spepcialchartc7() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@!!@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "123456")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@!!@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_Spepcialchar tc8
    @Test
    public void testLoginSubmit_Spepcialchartc8() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@!!@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("Password", "")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@!!@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_Spepcialchar tc9
    @Test
    public void testLoginSubmit_Spepcialchartc9() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("lequyet180902@!!@gmail.com")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "456")).thenReturn(false);
        String result = userController.loginSubmit("lequyet180902@!!@gmail.com", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_UsernameNull tc10
    @Test
    public void testLoginSubmit_UsernameNulltc10() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "123456")).thenReturn(false);
        String result = userController.loginSubmit("", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_UsernameNull tc11
    @Test
    public void testLoginSubmit_UsernameNulltc11() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("");
        when(userService.checkPassword("Password", "")).thenReturn(false);
        String result = userController.loginSubmit("", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    // testLoginSubmit_UsernameNull tc12
    @Test
    public void testLoginSubmit_UsernameNulltc12() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername("")).thenReturn(null);
        when(userDetails.getPassword()).thenReturn("123456");
        when(userService.checkPassword("Password", "456")).thenReturn(false);
        String result = userController.loginSubmit("", "Password", model);
        assertEquals("sign-in", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Email không tồn tại"));
    }

    //---------------------------------------------------------------------------------------------------------------------
    // testProcessForgotPassword_Success tc1
    @Test
    public void testProcessForgotPassword_Success() {
        doNothing().when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("lequyet180902@gmail.com", redirectAttributes, request, model);
        assertEquals("redirect:/forgot-password", result);

        verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), eq("Chúng tôi đã gửi Email về cho bạn"));
    }

    // testProcessForgotPassword_EmailNulltc2
    @Test
    public void testProcessForgotPassword_EmailNulltc2() {
        doThrow(new BadCredentialsException("Email không tồn tại")).when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
    }

    // testProcessForgotPassword_Spepcialcharacter tc3
    @Test
    public void testProcessForgotPassword_Spepcialcharactertc3() {
        doThrow(new BadCredentialsException("Email không tồn tại")).when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("lequyet180902@#@#gmail.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
    }

    // testProcessForgotPassword_MistakeEmail tc4
    @Test
    public void testProcessForgotPassword_MistakeEmailtc4() {
        doThrow(new BadCredentialsException("Email không tồn tại")).when(userService).sendResetPasswordEmail(anyString(), any(HttpServletRequest.class));
        String result = userController.processForgotPassword("lequyet18aa@gmail.com", redirectAttributes, request, model);
        assertEquals("forgot-password", result);
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("Chúng tôi không thấy có Email người dùng "));
    }

    //----------------------------------------------------------------------------------------------------------------------
    @Test
    public void testShowResetPasswordForm_ValidToken() {
        when(userService.isResetTokenValid("validToken")).thenReturn(true);
        String result = userController.showResetPasswordForm("validToken", model);
        assertEquals("reset-password", result);
        verify(model, times(1)).addAttribute("token", "validToken");
    }


    @Test
    public void testShowResetPasswordForm_InvalidToken() {
        when(userService.isResetTokenValid("invalidToken")).thenReturn(false);
        String result = userController.showResetPasswordForm("invalidToken", model);
        assertEquals("reset-password", result);
        verify(model, times(1)).addAttribute("errorMessage", "Mã thông báo đặt lại không hợp lệ hoặc đã hết hạn.");
    }
    //----------------------------------------------------------------------------------------------------------------------


    @Test
    public void testProcessResetPassword_ShortPassword() {
        String result = userController.processResetPassword("validToken", "123", "123", redirectAttributes, model);
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
