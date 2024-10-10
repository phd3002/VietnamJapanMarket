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

    //    List<CartItem> findByCart(Cart cart);
//
//    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.productId = :products ")
//    Optional<CartItem> findByCartAndProduct(@Param("cart") Cart cart, @Param("products") Products product);
//
//    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.user.userId = :userId")
//    List<CartItem> findCartItemsByUserId(@Param("userId") Integer userId);
//
//    List<CartItem> findByCartItemIdIn(List<Integer> cartItemIds);
//
//    @Query("SELECT ci FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
//    CartItem getCartItemByCartItemId(Integer cartItemId);
//
//    @Modifying
//    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId IN :ids")
//    void deleteByIds(@Param("ids") List<Integer> ids);
//
//    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.userId = :deliveryRoleId")
//    List<CartItem> findCartItemsByDeliveryRoleId(Integer deliveryRoleId);
    List<CartItem> findByCart_CartId(Integer cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") Integer userId);

    // Find a cart item by its cart ID and product ID
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    CartItem findByCartIdAndProductId(@Param("cartId") Integer cartId, @Param("productId") Products productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.variationId = :variation")
    CartItem findByCartIdAndVariation(@Param("cartId") Integer cartId, @Param("variation") ProductVariation variation);

//    CartItem findByCartIdAndProductId(Integer cartId, Integer productId);

    // Find cart items by a list of cart item IDs
    List<CartItem> findByCartItemIdIn(List<Integer> cartItemIds);

    // Find cart items by a delivery role ID
//    List<CartItem> findCartItemsByDeliveryRoleId(Integer deliveryRoleId);

    // Delete cart items by their IDs
//    void deleteById(List<Integer> cartItemId);

    // Delete a single cart item by its ID
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cartItemId = :cartItemId")
    void deleteByCartItemId(@Param("cartItemId") Integer cartItemId);
}
