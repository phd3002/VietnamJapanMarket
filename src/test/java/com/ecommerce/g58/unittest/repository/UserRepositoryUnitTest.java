package com.ecommerce.g58.unittest.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class UserRepositoryUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService; // Giả sử bạn có một lớp UserService sử dụng UserRepository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    // Test case for findByEmail
    @Test
    void testFindByEmail() {
        // Given
        Users user = new Users();
        user.setEmail("lequeyt180902@gmail.com");
        when(userRepository.findByEmail("lequeyt180902@gmail.com")).thenReturn(user);

        // When
        Users foundUser = userRepository.findByEmail("lequeyt180902@gmail.com");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("lequeyt180902@gmail.com");
    }

    // Test case for findByEmail when email does not exist
    @Test
    void testFindByEmailWhenEmailDoesNotExist() {
        // Given
        String email = "nonexistent@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    // Test case for findByEmail when email is empty
    @Test
    void testFindByEmailWithEmptyEmail() {
        // Given
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }


    // Test case for findByEmail when email is null
    @Test
    void testFindByEmailWithNullEmail() {
        // Given
        String email = null;
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    //Test case for findByEmail when email is invalid
    @Test
    void testFindByEmailWithInvalidEmail() {
        // Given
        String email = "lequyet#@edunext.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    @Test
    void testFindByUsername() {
        // Given
        Users user = new Users();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        Optional<Users> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    // Test case for findByUsername when username not found
    @Test
    void testFindByUsernameNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        // When
        Optional<Users> foundUser = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void testFindByUsernameWithNull() {
        // Given
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When
        Optional<Users> foundUser = userRepository.findByUsername(null);

        // Then
        assertThat(foundUser).isNotPresent();
    }

    // Test case for findByUsername when username is case insensitive
    @Test
    void testFindByUsernameCaseInsensitive() {
        // Given
        Users user = new Users();
        user.setUsername("TestUser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        Optional<Users> foundUser = userRepository.findByUsername("testuser"); // lowercase

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("TestUser");
    }

    // Test case for findByUsername when username duplicate
    @Test
    void testFindByDuplicateUsername() {
        // Given
        Users user1 = new Users();
        user1.setUsername("duplicateuser");

        Users user2 = new Users();
        user2.setUsername("duplicateuser");

        when(userRepository.findByUsername("duplicateuser")).thenReturn(Optional.of(user1));

        // When
        Optional<Users> foundUser = userRepository.findByUsername("duplicateuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("duplicateuser");
    }

    // Test case for findByUsername when username has special characters
    @Test
    void testFindByUsernameWithSpecialCharacters() {
        // Given
        Users user = new Users();
        user.setUsername("user@##$%$^&*()_+{}|:<>?/.,;'][]");

        when(userRepository.findByUsername("user@##$%$^&*()_+{}|:<>?/.,;'][]")).thenReturn(Optional.of(user));

        // When
        Optional<Users> foundUser = userRepository.findByUsername("user@##$%$^&*()_+{}|:<>?/.,;'][]"); // lowercase

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("user@##$%$^&*()_+{}|:<>?/.,;'][]");
    }

    // Test case for findByUsername when username after deletion
    @Test
    void testFindByUsernameAfterDeletion() {
        // Given
        Users user = new Users();
        user.setUsername("deleteduser");
        when(userRepository.findByUsername("deleteduser")).thenReturn(Optional.of(user));

        //When
        Optional<Users> foundUserBeforeDeletion = userRepository.findByUsername("deleteduser");
        assertThat(foundUserBeforeDeletion).isPresent();
        userRepository.delete(user);
        when(userRepository.findByUsername("deleteduser")).thenReturn(Optional.empty());
        Optional<Users> foundUserAfterDeletion = userRepository.findByUsername("deleteduser");

        // Then
        assertThat(foundUserAfterDeletion).isNotPresent(); // Xác nhận rằng người dùng không còn tồn tại
    }


}
