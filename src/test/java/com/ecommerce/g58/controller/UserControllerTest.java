package com.ecommerce.g58.controller;

import com.ecommerce.g58.security.SecurityConfig; // Adjust the import as necessary
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.entity.Users; // Assuming this is your Users model
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private UserController userController; // Replace with your actual controller class

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_PasswordsDoNotMatch() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        String result = userController.registerUser(user, "differentPassword", model, redirectAttributes, request);

        verify(model).addAttribute("errorMessage", "Mật khẩu và mật khẩu xác nhận không khớp.");
        assertEquals("sign-up", result);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userService.isEmailExist(user.getEmail())).thenReturn(true);

        String result = userController.registerUser(user, "password", model, redirectAttributes, request);

        verify(model).addAttribute("errorMessage", "Email đã tồn tại.");
        assertEquals("sign-up", result);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userService.isEmailExist(user.getEmail())).thenReturn(false);

        String result = userController.registerUser(user, "password",  model, redirectAttributes,request);

        verify(request.getSession()).setAttribute("otp", any());
        assertEquals("redirect:/sign-up/confirm-code", result);
    }

    @Test
    void testRegisterUser_ExceptionHandling() throws Exception {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userService.isEmailExist(user.getEmail())).thenThrow(new RuntimeException("Database error"));

        String result = userController.registerUser(user, "password", model, redirectAttributes, request);

        verify(redirectAttributes).addFlashAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại.");
        assertEquals("redirect:/sign-up", result);
    }
}
