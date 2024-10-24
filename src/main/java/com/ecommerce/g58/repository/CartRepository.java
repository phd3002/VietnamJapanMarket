package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer>{
    // Find a cart by the associated user
    Cart findByUser(Users user);

    // Find a cart by the user ID
    Cart findByUser_UserId(Integer userId);

}
