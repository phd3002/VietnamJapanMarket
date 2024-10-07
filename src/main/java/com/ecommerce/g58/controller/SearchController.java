package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/api/search")
    public List<Products> searchProducts(@RequestParam("query") String query) {
        System.out.println("Received search query: " + query);
        List<Products> results = productService.searchProducts(query);
        System.out.println("Search results: " + results);
        return results;
    }
}