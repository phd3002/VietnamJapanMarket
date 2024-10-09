package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer>{
    Optional<Cart> findByUser(Users user);

    @Query("SELECT SUM(ci.quantity) FROM Cart c JOIN CartItem ci ON c.cartId = ci.cartItemId WHERE c.user = :userId ")
    Integer getTotalQuantityByUser(@Param("userId") Users userId);


}
