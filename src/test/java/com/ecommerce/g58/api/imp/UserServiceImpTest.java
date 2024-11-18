package com.ecommerce.g58.api.imp;

import com.ecommerce.g58.entity.ResetToken;
import com.ecommerce.g58.entity.Roles;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.ResetTokenRepository;
import com.ecommerce.g58.repository.RoleRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.implementation.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {UserServiceImp.class})
public class UserServiceImpTest {

    @Autowired
    private UserServiceImp userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ResetTokenRepository resetTokenRepository;

    @MockBean
    private JavaMailSender mailSender;

    private Users user;
    private Roles role;

    @BeforeEach
    public void setUp() {
        user = new Users();
        user.setEmail("lequyet180902@gmail.com");
        user.setPassword("password123");

        role = new Roles();
        role.setRoleId(3);
        role.setRoleName("ROLE_USER");
    }

    @Test
    public void testFindByEmail() {
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        Users foundUser = userService.findByEmail("lequyet180902@gmail.com");
        assertEquals(user, foundUser);
    }

    @Test
    public void testIsEmailExist() {
        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
        assertTrue(userService.isEmailExist("lequyet180902@gmail.com"));
    }

    @Test
    public void testRegisterUser() {
        when(roleRepository.findByRoleId(3)).thenReturn(role);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.registerUser(user);

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRoleId());
        verify(userRepository, times(1)).save(user);
    }

//    @Test
//    public void testLoadUserByUsername() {
//        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
//        assertEquals(user, userService.loadUserByUsername("lequyet1809@mail.com"));
//    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("invalid@gmail.com")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("invalid@gmail.com");
        });
    }

    @Test
    public void testCheckPassword() {
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        assertTrue(userService.checkPassword("rawPassword", "encodedPassword"));
    }

//    @Test
//    public void testSendResetPasswordEmail() throws MessagingException {
//        when(userRepository.findByEmail("lequyet180902@gmail.com")).thenReturn(user);
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
//        when(request.getServletPath()).thenReturn("/sign-in");
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        userService.sendResetPasswordEmail("lequyet180902@gmail.com", request);
//
//        verify(mailSender, times(1)).send(mimeMessage);
//    }

    @Test
    public void testIsResetTokenValid() {
        ResetToken resetToken = new ResetToken(UUID.randomUUID().toString(), user);
        when(resetTokenRepository.findByToken(resetToken.getToken())).thenReturn(resetToken);
        assertTrue(userService.isResetTokenValid(resetToken.getToken()));
    }

    @Test
    public void testUpdatePasswordReset() {
        ResetToken resetToken = new ResetToken(UUID.randomUUID().toString(), user);
        when(resetTokenRepository.findByToken(resetToken.getToken())).thenReturn(resetToken);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.updatePasswordReset(resetToken.getToken(), "newPassword");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(resetTokenRepository, times(1)).delete(resetToken);
    }

    @Test
    public void testUpdatePassword() {
        when(passwordEncoder.encode("123")).thenReturn("encodedNewPassword");

        userService.updatePassword(user, "123");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }
}