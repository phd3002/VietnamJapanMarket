package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.implementation.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountControllerTest {
    @Mock
    private ProfileService profileService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testMyAccountPost() {
        // Arrange
        String email = "test@example.com";
        when(userDetails.getUsername()).thenReturn(email);

        Users testUser = new Users();  // Assuming a User class
        testUser.setUserId(1);
        testUser.setEmail(email);

        when(profileService.getUserByEmail(email)).thenReturn(testUser);

        // Act
        accountController.myAccountPost(
                "John", "Doe", "new-email@example.com",
                "123456789", "oldPass", "newPass",
                userDetails
        );

        // Assert
        verify(profileService, times(1)).updateUser(
                1, "John", "Doe", "new-email@example.com",
                "123456789", "oldPass", "newPass"
        );
    }
}