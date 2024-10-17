package com.ecommerce.g58.service;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wishlist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    void addProductToWishlist(Users user, ProductDetailDTO productDetailDTO);

    List<Wishlist> getUserWishlist(Integer userId); // New method to get user wishlist
}
