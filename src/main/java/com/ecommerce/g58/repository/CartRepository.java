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


//    @Query("SELECT c FROM Cart c WHERE c.user = :user")
//    Optional<Cart> findByUser(Users user);
//
//    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
//    Optional<Cart> findByUserId(@Param("userId") Integer userId);
//
//    @Query("SELECT SUM(ci.quantity) FROM Cart c JOIN CartItem ci ON c.cartId = ci.cartItemId WHERE c.user = :userId ")
//    Integer getTotalQuantityByUser(@Param("userId") Users userId);
//
//    @Query("SELECT c.cartId FROM Cart c WHERE c.user.userId = :userId ")
//    Integer findCartIdByUserId(@Param("userId") Integer userId);
//
//    @Query("SELECT SUM(c.totalPrice) FROM Cart c JOIN c.cartItems ci WHERE c.cartId = :cartId")
//    Integer getTotalFoodPriceByCartId(@Param("cartId") Integer cartId);
//
//    @Query("SELECT c FROM Cart c WHERE c.user.userId = :deliveryRoleId ")
//    List<Cart> findListCartsByDeliveryRoleIdAndStatusProvisional(Integer deliveryRoleId);
//
//    @Query("SELECT c FROM Cart c WHERE c.user.userId = :deliveryRoleId")
//    Cart findCartsByDeliveryRoleIdAndStatusProvisional(Integer deliveryRoleId);


}
