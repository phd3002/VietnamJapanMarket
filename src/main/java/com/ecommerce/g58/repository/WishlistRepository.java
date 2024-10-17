package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    // Find a wishlist by the associated user
//    Wishlist findByUserId(Users user);

    // Find a wishlist by the user ID
//    Wishlist findByUserId(Users userId);

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId AND w.productVariation = :productVariation")
    Optional<Wishlist> findByUserIdAndProductVariation(@Param("userId") Integer userId, @Param("productVariation") ProductVariation productVariation);

    @Query("SELECT w FROM Wishlist w WHERE w.userId = :userId")
    List<Wishlist> findByUser(@Param("user") Integer userId); // Added method to find wishlist items by user

}
