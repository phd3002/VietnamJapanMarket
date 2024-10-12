package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductDTO;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/api/search")
    public List<ProductDTO> searchProducts(@RequestParam("query") String query) {
        logger.info("Received search query: {}", query);
        // Fetch the search results mapped to ProductDTO
        List<ProductDTO> results = productService.searchProducts(query);
        logger.info("Search results returned: {}", results.size());
        return results;
    }
}