package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JavaMailSender mailSender;

    private Users user;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
    }

    // Test hiển thị form đăng ký
//    @Test
//    public void testShowRegistrationForm() throws Exception {
//        mockMvc.perform(get("/sign-up"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("sign-up"))
//                .andExpect(model().attributeExists("users"));
//    }

    // Test đăng ký người dùng mới
//    @Test
//    public void testRegisterUser_Success() throws Exception {
//        when(userService.isEmailExist(anyString())).thenReturn(false);
//
//        mockMvc.perform(post("/sign-up")
//                        .param("email", "test@example.com")
//                        .param("password", "password")
//                        .param("confirmPassword", "password")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/sign-up/confirm-code"));
//
//        verify(userService, never()).registerUser(any(Users.class));
//    }

//    // Test nếu mật khẩu và mật khẩu xác nhận không khớp
//    @Test
//    public void testRegisterUser_PasswordMismatch() throws Exception {
//        mockMvc.perform(post("/sign-up")
//                        .param("email", "test@example.com")
//                        .param("password", "password")
//                        .param("confirmPassword", "differentPassword")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(view().name("sign-up"))
//                .andExpect(model().attributeExists("errorMessage"));
//    }


    // Test xác nhận OTP không hợp lệ
    @Test
    public void testProcessVerifyOtp_InvalidOtp() throws Exception {
        mockMvc.perform(post("/sign-up/confirm-code")
                        .sessionAttr("otp", "123456")
                        .param("otp", "654321"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sign-up/confirm-code"))
                .andExpect(flash().attributeExists("error"));
    }

    // Test hiển thị form đăng nhập
    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/sign-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("sign-in"));
    }



    // Test quá trình quên mật khẩu
    @Test
    public void testProcessForgotPassword_Success() throws Exception {
        doNothing().when(userService).sendResetPasswordEmail(anyString(), any());

        mockMvc.perform(post("/forgot-password")
                        .param("email", "equyet180902@gmail.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

    // Test hiển thị form reset mật khẩu
    @Test
    public void testShowResetPasswordForm() throws Exception {
        when(userService.isResetTokenValid(anyString())).thenReturn(true);

        mockMvc.perform(get("/reset-password")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("token"));
    }

    // Test reset mật khẩu thành công
    @Test
    public void testProcessResetPassword_Success() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("token", "valid-token")
                        .param("password", "newpassword")
                        .param("confirmPassword", "newpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

}
