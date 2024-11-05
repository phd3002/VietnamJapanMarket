package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Countries;
import com.ecommerce.g58.repository.CountryRepository;
import com.ecommerce.g58.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImp implements CountryService {
    @Autowired
    private CountryRepository countryRepository;

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries getCountryById(Integer countryId) {
        return countryRepository.findById(countryId).orElseThrow(() -> new RuntimeException("Country not found"));
    }

    public Optional<Countries> findById(Integer countryId) {
        return countryRepository.findById(countryId);
    }

}
