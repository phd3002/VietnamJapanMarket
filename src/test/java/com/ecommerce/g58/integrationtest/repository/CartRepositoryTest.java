package com.ecommerce.g58.integrationtest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.g58.repository.UserRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository usersRepository; // Giả định rằng bạn có một UsersRepository để lưu người dùng

    private Users user;

    @BeforeEach
    void setUp() {
        // Thiết lập người dùng và giỏ hàng trước mỗi test case
        user = new Users();
        user.setUsername("customer1");
        user.setEmail("customer1@gamil.com");
        usersRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

    // Test case for findByUser
    @Test
    void testFindByUser() {
        // When
        Cart foundCart = cartRepository.findByUser(user);

        // Then
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getUser()).isEqualTo(user);
    }

    // Test case for findByUser when user not found
    @Test
    void testFindByUserNotFound() {
        // Given
        Users anotherUser = new Users();
        anotherUser.setUsername("lequyet");
        anotherUser.setEmail("lequyet@gmail.com");

        // When
        Cart foundCart = cartRepository.findByUser(anotherUser);

        // Then
        assertThat(foundCart).isNull();
    }

    // Test case for findByUser_UserId
    @Test
    void testFindByUser_UserId() {
        // When
        Cart foundCart = cartRepository.findByUser_UserId(user.getUserId());

        // Then
        assertThat(foundCart).isNotNull();
        assertThat(foundCart.getUser().getUserId()).isEqualTo(user.getUserId());
    }

    // Test case for findByUser_UserId when user ID not found
    @Test
    void testFindByUser_UserIdNotFound() {
        // Given
        Integer nonexistentUserId = 999; // Một ID người dùng không tồn tại

        // When
        Cart foundCart = cartRepository.findByUser_UserId(nonexistentUserId);

        // Then
        assertThat(foundCart).isNull();
    }
}
