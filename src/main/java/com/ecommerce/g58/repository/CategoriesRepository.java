package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
//    Categories findByCategoryName(String categoryName);
//    Page<Categories> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);
}
