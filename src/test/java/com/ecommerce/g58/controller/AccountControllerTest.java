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
    private AccountController accountController;
    @Mock
    private Model model;  // Mock the Model
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMyAccountView() {
        String viewName = accountController.myAccount(model);
        assertEquals("my-account", viewName);
    }
}