package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Size;
import com.ecommerce.g58.repository.SizeRepository;
import com.ecommerce.g58.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeServiceImp implements SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }
    @Override
    public Optional<Size> findById(Integer id) {
        return sizeRepository.findById(id);
    }
}
