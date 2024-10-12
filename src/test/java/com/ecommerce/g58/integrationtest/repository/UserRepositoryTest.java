package com.ecommerce.g58.integrationtest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    //Test case for findByEmail
    @Test
    void testFindByEmail() {
        // Given
        Users user = new Users();
        user.setEmail("lequeyt180902@gmail.com");
        userRepository.save(user);

        // When
        Users foundUser = userRepository.findByEmail("lequeyt180902@gmail.com");

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("lequeyt180902@gmail.com");
    }

    //Test case for findByEmail when email does not exist
    @Test
    void testFindByEmailWhenEmailDoesNotExist() {
        // Given
        String email = "nonexistent@gmail.com";

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    //Test case for findByEmail when email is empty
    @Test
    void testFindByEmailWithEmptyEmail() {
        // Given
        String email = "";

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    //Test case for findByEmail when email is null
    @Test
    void testFindByEmailWithNullEmail() {
        // Given
        String email = null;

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    //Test case for findByEmail when email is invalid
    @Test
    void testFindByEmailWithInvalidEmail() {
        // Given
        String email = "lequyet#@gmail.com";

        // When
        Users foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNull();
    }

    //Test case for findByUsername
    @Test
    void testFindByUsername() {
        // Given
        Users user = new Users();
        user.setUsername("testuser");
        userRepository.save(user);

        // When
        Optional<Users> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    //Test case for findByUsername when username not found
    @Test
    void testFindByUsernameNotFound() {
        // Given
        Users user = new Users();
        user.setUsername("nonexistentuser");
        userRepository.save(user);
        // When
        Optional<Users> foundUser = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(foundUser).isNotPresent();
    }

    //Test case for findByUsername when username is null
    @Test
    void testFindByUsernameWithNull() {
        // When
        Optional<Users> foundUser = userRepository.findByUsername(null);

        // Then
        assertThat(foundUser).isNotPresent();
    }

    //Test case for findByUsername when username iscase insensitive
    @Test
    void testFindByUsernameCaseInsensitive() {
        // Given
        Users user = new Users();
        user.setUsername("TestUser");
        userRepository.save(user);

        // When
        Optional<Users> foundUser = userRepository.findByUsername("testuser"); // lowercase

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("TestUser");
    }

    //Test case for findByUsername when username duplicate
    @Test
    void testFindByDuplicateUsername() {
        // Given
        Users user1 = new Users();
        user1.setUsername("duplicateuser");
        userRepository.save(user1);

        Users user2 = new Users();
        user2.setUsername("duplicateuser");
        userRepository.save(user2);

        // When
        Optional<Users> foundUser = userRepository.findByUsername("duplicateuser");

        // Then
        assertThat(foundUser).isPresent();
    }

    //Test case for findByUsername when username has special characters
    @Test
    void testFindByUsernameWithSpecialCharacters() {
        // Given
        Users user = new Users();
        user.setUsername("user@##$%$^&*()_+{}|:<>?/.,;'][");
        userRepository.save(user);

        // When
        Optional<Users> foundUser = userRepository.findByUsername("user@##$%$^&*()_+{}|:<>?/.,;']["); // lowercase

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("user@##$%$^&*()_+{}|:<>?/.,;'][");
    }

    //Test case for findByUsername when username after deletion
    @Test
    void testFindByUsernameAfterDeletion() {
        // Given
        Users user = new Users();
        user.setUsername("deleteduser");
        userRepository.save(user);
        userRepository.delete(user);

        // When
        Optional<Users> foundUser = userRepository.findByUsername("deleteduser");

        // Then
        assertThat(foundUser).isNotPresent();
    }

}
