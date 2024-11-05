package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Color;
import com.ecommerce.g58.repository.ColorRepository;
import com.ecommerce.g58.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorServiceImp implements ColorService {
    @Autowired
    private ColorRepository colorRepository;

    @Override
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    @Override
    public Optional<Color> findById(Integer id) {
        return colorRepository.findById(id);
    }
}
