package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Categories;
import com.ecommerce.g58.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll(); // Fetch all categories
    }
}
