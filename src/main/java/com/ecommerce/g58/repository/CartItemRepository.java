package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer>{

    List<CartItem> findByCart(Cart cart);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.productId = :products ")
    Optional<CartItem> findByCartAndProduct(@Param("cart")Cart cart, @Param("products")Products product);
}
