package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Optional;

@Controller
//@RequestMapping("/seller/seller-management")
public class ProductManagementController {
    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private FileS3Service fileS3Service;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private ColorService colorService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Size.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Size size = sizeService.findById(Integer.parseInt(text)).orElse(null);
                setValue(size);
            }
        });

        binder.registerCustomEditor(Color.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Color color = colorService.findById(Integer.parseInt(text)).orElse(null);
                setValue(color);
            }
        });
    }

    @GetMapping("/seller-products/{storeId}")
    public String getProductsByStore(@PathVariable Integer storeId, Model model) {
        Stores store = new Stores();
        store.setStoreId(storeId);
        List<Products> products = productService.getProductsByStoreId(store);
        model.addAttribute("products", products);
        return "seller/product-manager";
    }

    @GetMapping("/edit-product/{productId}")
    public String editProduct(@PathVariable("productId") Integer productId, Model model) {
        Optional<Products> product = productService.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            // Assuming you have categories to populate in the dropdown
            model.addAttribute("categories", categoriesService.getAllCategories());
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm");
        }
        return "seller/update-product"; // This should be the path to your Thymeleaf template for updating products
    }

    // Method to update product information
    @PostMapping("/edit-product/{productId}")
    public String updateProduct(@PathVariable("productId") Integer productId, @RequestParam String productName,
                                @RequestParam String productDescription, @RequestParam Integer price,
                                @RequestParam float weight, @RequestParam Integer categoryId,
                                RedirectAttributes redirectAttributes) {
        Optional<Products> optionalProduct = productService.findById(productId);
        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            product.setProductName(productName);
            product.setProductDescription(productDescription);
            product.setPrice(price);
            product.setWeight(weight);
            product.setCategoryId(categoriesService.findById(categoryId).orElse(null));
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
        }
        return "redirect:/edit-product/" + productId;
    }

    @GetMapping("/addProductForm/{storeId}")
    public String showAddProductForm(@PathVariable Integer storeId, Model model, HttpSession session) {
        List<Categories> categories = categoriesService.getAllCategories();
        Optional<Stores> store = storeService.findById(storeId);
        List<Size> sizes = sizeService.getAllSizes();
        List<Color> colors = colorService.getAllColors();
        model.addAttribute("sizes", sizes);
        model.addAttribute("colors", colors);
        model.addAttribute("categories", categories);
        Products product = new Products();
        if (store.isPresent()) {
            model.addAttribute("store", store.get());
            session.setAttribute("storeId", storeId);
        } else {
            model.addAttribute("error", "Không tìm thấy cửa hàng");
        }

        model.addAttribute("product", product);
        model.addAttribute("productImage", new ProductImage());
        model.addAttribute("productVariation", new ProductVariation());
        return "seller/add-product";
    }

    @PostMapping("/addProduct")
    public String addProduct(@ModelAttribute Products product, HttpSession session, Model model,
                             RedirectAttributes redirectAttributes) {
        Integer storeId = (Integer) session.getAttribute("storeId");
        if (storeId != null) {
            Optional<Stores> store = storeService.findById(storeId);
            store.ifPresent(product::setStoreId);
        }
        productService.addProduct(product);

        // Repopulate categories and product attributes to retain the filled-in values
        List<Categories> categories = categoriesService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product); // Keep the filled-in product details
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        return "redirect:/addProductForm/" + storeId;
    }


    @PostMapping("/addProductImage")
    public String addProductImage(@RequestParam("thumbnail") MultipartFile thumbnail,
                                  @RequestParam("firstImage") MultipartFile firstImage,
                                  @RequestParam("secondImage") MultipartFile secondImage,
                                  @RequestParam("thirdImage") MultipartFile thirdImage,
                                  @RequestParam("imageName") String imageName,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session, Model model) {

        Integer storeId = (Integer) session.getAttribute("storeId");

        ProductImage productImage = new ProductImage();
        productImage.setImageName(imageName);

        try {
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailUrl = fileS3Service.uploadFile(thumbnail);
                productImage.setThumbnail(thumbnailUrl);
            }
            if (firstImage != null && !firstImage.isEmpty()) {
                String firstImageUrl = fileS3Service.uploadFile(firstImage);
                productImage.setImage1(firstImageUrl);
            }
            if (secondImage != null && !secondImage.isEmpty()) {
                String secondImageUrl = fileS3Service.uploadFile(secondImage);
                productImage.setImage2(secondImageUrl);
            }
            if (thirdImage != null && !thirdImage.isEmpty()) {
                String thirdImageUrl = fileS3Service.uploadFile(thirdImage);
                productImage.setImage3(thirdImageUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to upload images. Please try again.");
            return "seller/add-product";
        }

        // Save product image to the database
        productService.addProductImage(productImage);

        // Repopulate form data
        model.addAttribute("productImage", productImage);
        model.addAttribute("categories", categoriesService.getAllCategories());
        redirectAttributes.addFlashAttribute("successMessageImg", "Thêm ảnh sản phẩm thành công!");
        return "redirect:/addProductForm/" + storeId;
    }


    @PostMapping("/addProductVariation")
    public String addProductVariation(@ModelAttribute ProductVariation productVariation,
                                      HttpSession session, Model model,
                                      RedirectAttributes redirectAttributes) {
        Integer storeId = (Integer) session.getAttribute("storeId");
        Products maxProductId = productService.getMaxProductId();
        ProductImage maxImageId = productService.getMaxImageId();
        productVariation.setProductId(maxProductId);
        productVariation.setImageId(maxImageId);
        productService.addProductVariation(productVariation);
        model.addAttribute("productVariation", productVariation);
        model.addAttribute("categories", categoriesService.getAllCategories());
        redirectAttributes.addFlashAttribute("successMessageVar", "Thêm biến thể sản phẩm thành công!");
        return "redirect:/addProductForm/" + storeId;
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
