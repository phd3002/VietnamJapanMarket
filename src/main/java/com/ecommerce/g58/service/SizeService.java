package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Size;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SizeService {
    List<Size> getAllSizes();

    Optional<Size> findById(Integer id);
}
