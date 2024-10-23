package com.ecommerce.g58.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;

public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;  // Inject the controller

    @Mock
    private Model model;  // Mock the Model

    @BeforeEach
    public void setUp() {
        // Initialize the mocks
        MockitoAnnotations.initMocks(this);
    }

    // Test: Ensure the "my-account" view is returned correctly
    @Test
    public void testMyAccountView() {
        // Execute the method
        String viewName = accountController.myAccount(model);

        // Verify the result (ensure the view name is "my-account")
        assertEquals("my-account", viewName);
    }
}