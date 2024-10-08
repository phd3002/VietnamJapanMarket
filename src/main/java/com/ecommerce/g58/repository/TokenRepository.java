package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Integer> {
    Tokens findByToken(String tokenValue);
}
