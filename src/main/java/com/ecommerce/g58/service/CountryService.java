package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Countries;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CountryService {
    List<Countries> getAllCountries();
    Countries getCountryById(Integer countryId);

}
