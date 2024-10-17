package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.ProductDetailDTO;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.entity.Wishlist;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.repository.WishlistRepository;
import com.ecommerce.g58.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImp implements WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Override
    public void addProductToWishlist(Users user, ProductDetailDTO productDetailDTO) {
        // Fetch the product variation as an entity from the database
        ProductVariation productVariation = productVariationRepository.findById(productDetailDTO.getVariationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid variation ID"));

        // Check if the product variation is already in the wishlist
        Optional<Wishlist> existingWishlistItem = wishlistRepository.findByUserIdAndProductVariation(user.getUserId(), productVariation);
        if (existingWishlistItem.isPresent()) {
            throw new RuntimeException("Product is already in the wishlist");
        }

        // If the item is not in the wishlist, create a new Wishlist entry
        Wishlist wishlist = Wishlist.builder()
                .userId(user)
                .product(productVariation.getProductId())
                .productVariation(productVariation)
                .addedAt(LocalDateTime.now())
                .build();

        wishlistRepository.save(wishlist);
    }

    @Override
    public List<Wishlist> getUserWishlist(Integer userId) {
        return wishlistRepository.findByUser(userId);
    }
}

