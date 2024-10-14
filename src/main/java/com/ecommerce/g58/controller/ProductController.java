package com.ecommerce.g58.controller;

import com.ecommerce.g58.dto.ProductVariationDTO;
import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product-list")
    public String productList(Model model) {
        List<ProductVariationDTO> products = productService.getAllDistinctProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/product-detail/{productId}")
    public String getProductDetail(@PathVariable Integer productId, Model model) {
        List<ProductVariationDTO> productVariations = productService.getProductDetailById(productId);

        // Lấy danh sách màu sắc và kích thước
        Set<String> colors = productVariations.stream().map(ProductVariationDTO::getColor).collect(Collectors.toSet());
        Set<String> sizes = productVariations.stream().map(ProductVariationDTO::getSize).collect(Collectors.toSet());

        // Lấy sản phẩm đầu tiên để hiển thị thông tin cơ bản
        ProductVariationDTO mainProduct = productVariations.get(0);

        model.addAttribute("product", mainProduct);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);
        return "product-detail";  // Tên của template product-detail
    }





    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam("categoryId") Integer categoryId, Model model) {
        List<ProductVariationDTO> products = productService.getProductsByCategory(categoryId);
        model.addAttribute("products", products);
        return "product-list";
    }
}
