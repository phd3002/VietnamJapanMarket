package com.ecommerce.g58.converter;

import com.ecommerce.g58.entity.Categories;
import com.ecommerce.g58.repository.CategoriesRepository;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;
@Component
public class CategoryConverter implements Converter<String, Categories> {
    private final CategoriesRepository categoryRepository;

    public CategoryConverter(CategoriesRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Categories convert(String source) {
        return categoryRepository.findById(Integer.parseInt(source))
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + source));
    }
}
