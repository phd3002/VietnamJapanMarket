package com.ecommerce.g58.controller;

import com.ecommerce.g58.repository.StoreRepository;
//import com.ecommerce.g58.repository.StoresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ViewStoreController {
    private final StoreRepository storesRepository;

    @GetMapping("/view-store/{id}")
    public String viewStore(@PathVariable Integer id, Model model) {
        var store = storesRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Store with id = " + id + " not found!"));
        model.addAttribute("store", store);
        return "view-store";
    }
}
