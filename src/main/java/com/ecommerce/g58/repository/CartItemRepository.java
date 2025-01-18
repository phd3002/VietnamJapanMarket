package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCart_CartId(Integer cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT ci.cartItemId FROM CartItem ci WHERE ci.cart.user.userId = :userId")
    List<Integer> findCartItemIdByUser(@Param("userId") Integer userId);

    // Find a cart item by its cart ID and product ID
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    CartItem findByCartIdAndProductId(@Param("cartId") Integer cartId, @Param("productId") Products productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.variationId = :variation")
    CartItem findByCartIdAndVariation(@Param("cartId") Integer cartId, @Param("variation") ProductVariation variation);

    // In CartItemRepository.java
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.variationId = :variationId")
    CartItem findByCartIdAndVariationId(@Param("cartId") Integer cartId, @Param("variationId") Integer variationId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.productId = :product AND ci.variationId = :variation")
    CartItem findByCartAndProductAndVariation(@Param("cart") Cart cart, @Param("product") Products product, @Param("variation") ProductVariation variation);


    // Find cart items by a list of cart item IDs
    List<CartItem> findByCartItemIdIn(List<Integer> cartItemIds);


    // Delete a single cart item by its ID
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
    void deleteByCartItemId(@Param("cartItemId") Integer cartItemId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.userId = :userId")
    int countCartItemsByUserId(@Param("userId") Integer userId);

    void deleteByProductId(Products productId);
    void deleteByVariationId(ProductVariation variationId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId IN :ids")
    void deleteByIds(@Param("ids") List<Integer> ids);

}
