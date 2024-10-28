package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.service.ProductService;
import com.ecommerce.g58.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
//@RequestMapping("/seller/seller-management")
public class ProductManagementController {
    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @GetMapping("/seller-products/{storeId}")
    public String getProductsByStore(@PathVariable Integer storeId, Model model) {
        Stores store = new Stores();
        store.setStoreId(storeId);
        List<Products> products = productService.getProductsByStoreId(store);
        model.addAttribute("products", products);
        return "seller/product-manager";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Products product) {
        productService.saveProduct(product);
        return "redirect:/seller-products/" + product.getStoreId().getStoreId();
    }

    @GetMapping("/delete-product/{productId}")
    public String deleteProduct(@PathVariable Integer productId, @RequestParam Integer storeId, HttpServletRequest request) {
//        Optional<Stores> optionalStore = storeService.findById(storeId);
        productService.deleteProductById(productId);
        String referer = request.getHeader("Referer");
//        return "redirect:" + referer;
        return "redirect:" + (referer != null ? referer : "/seller-products/" + storeId);
    }

}
