package com.ecommerce.g58.integrationtest.repository;
import com.ecommerce.g58.entity.ResetToken;
import com.ecommerce.g58.repository.ResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ResetTokenTest {
    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @BeforeEach
    void setUp() {
        ResetToken token = new ResetToken();
        token.setToken("sample_token");
        resetTokenRepository.save(token);
    }

    @Test
    void testFindByToken() {
        // Given
        String tokenString = "sample_token";

        // When
        ResetToken foundToken = resetTokenRepository.findByToken(tokenString);

        // Then
        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getToken()).isEqualTo(tokenString);
    }

    @Test
    void testFindByTokenNotFound() {
        // Given
        String tokenString = "non_existent_token";

        // When
        ResetToken foundToken = resetTokenRepository.findByToken(tokenString);

        // Then
        assertThat(foundToken).isNull(); // Kiểm tra rằng không tìm thấy token
    }
}
