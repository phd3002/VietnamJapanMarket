package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Color;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ColorService {
    List<Color> getAllColors();

    Optional<Color> findById(Integer id);
}
