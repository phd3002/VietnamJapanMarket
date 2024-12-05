package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.repository.ProductRepository;
import com.ecommerce.g58.repository.StoreRepository;
//import com.ecommerce.g58.repository.StoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewStoreController {
    private final StoreRepository storesRepository;
    private final ProductRepository productRepository;

    @GetMapping("/view-store/{id}")
    public String viewStore(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            Model model) {
        // Fetch the store
        Stores store = storesRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Store with id = " + id + " not found!"));

        // Create Pageable instance
        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated products
        Page<com.ecommerce.g58.entity.Products> productPage = productRepository.findByStoreIdStoreId(id, pageable);

        // Add attributes to the model
        model.addAttribute("store", store);
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("size", productPage.getSize());
        model.addAttribute("totalItems", productPage.getTotalElements());

        return "view-store";
    }

}
