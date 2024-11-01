package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Countries, Integer> {
}
