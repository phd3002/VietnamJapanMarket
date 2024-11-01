package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.repository.CountriesRepository;
import com.ecommerce.g58.service.CountriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountriesServiceImpl implements CountriesService {

    @Autowired
    private CountriesRepository countriesRepository;

    @Override
    public List<Countries> getAllCountries() {
        return countriesRepository.findAll();
    }
}
