package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RequestMapping("/seller/seller-management")
public class ProductManagementController {
    private final ProductService productService;

    @Autowired
    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/seller-products/{storeId}")
    public String getProductsByStore(@PathVariable Integer storeId, Model model) {
        Stores store = new Stores();
        store.setStoreId(storeId);
        List<Products> products = productService.getProductsByStoreId(store);
        model.addAttribute("products", products);
        return "seller/product-manager";
    }

//    @PostMapping("/add-product")
//    public String addProduct(@ModelAttribute Products product) {
//        productService.saveProduct(product);
//        return "redirect:/api/seller-management/seller-products?storeId=" + product.getStoreId().getStoreId();
//    }
}
