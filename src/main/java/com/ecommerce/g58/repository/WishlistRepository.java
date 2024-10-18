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
    @Query("SELECT w FROM Wishlist w WHERE w.userId = :user AND w.productVariation = :productVariation")
    Optional<Wishlist> findByUserAndProductVariation(@Param("user") Users user, @Param("productVariation") ProductVariation productVariation);


    @Query("SELECT w FROM Wishlist w WHERE w.userId = :user")
    List<Wishlist> findByUser(@Param("user") Users user);

}
