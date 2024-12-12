package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.ProductVariationRepository;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class ProductManagementController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductVariationRepository productVariationRepository;

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

        binder.registerCustomEditor(Products.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Products product = productService.findById(Integer.parseInt(text)).orElse(null);
                setValue(product);
            }
        });
    }

    @GetMapping("/seller-products")
    public String getProductsByStore(Model model, @RequestParam(defaultValue = "0") int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Ensure the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Users owner = userRepository.findByEmail(authentication.getName());
        Optional<Stores> storeOwner = storeRepository.findByOwnerId(owner);
        Pageable pageable = PageRequest.of(page, 4);
        Page<Products> productsPage = productService.getProductsByStoreId(pageable);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("storeId", storeOwner.get().getStoreId());
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
    @PostMapping("/update-product/{productId}")
    public String updateProduct(@PathVariable("productId") Integer productId, @RequestParam String productName,
                                @RequestParam String productDescription, @RequestParam Integer price,
                                @RequestParam float weight,
                                RedirectAttributes redirectAttributes) {
        Optional<Products> optionalProduct = productService.findById(productId);

        // Kiểm tra các thông tin đầu vào
        if (productName == null || productName.isEmpty() || productName.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
            return "redirect:/edit-product/" + productId;
        }
        if (productDescription == null || productDescription.isEmpty() || productDescription.length() > 500) {
            redirectAttributes.addFlashAttribute("error", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
            return "redirect:/edit-product/" + productId;
        }
        if (price == null || price < 20000 || price > 50000000) {
            redirectAttributes.addFlashAttribute("error", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
            return "redirect:/edit-product/" + productId;
        }
        if (weight < 0.1 || weight > 20.0) {
            redirectAttributes.addFlashAttribute("error", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
            return "redirect:/edit-product/" + productId;
        }

        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            product.setProductName(productName);
            product.setProductDescription(productDescription);
            product.setPrice(price);
            product.setWeight(weight);
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
        }

        return "redirect:/edit-product/" + productId;
    }

    @GetMapping("/addProductForm2/{storeId}")
    public String showAddProductForm2(@PathVariable Integer storeId, Model model, HttpSession session) {
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

        model.addAttribute("product2", product);
        model.addAttribute("productImage2", new ProductImage());
        model.addAttribute("productVariation2", new ProductVariation());
        return "seller/add-product2";
    }

    @PostMapping("/addProductFull")
    public String addProductFull(@ModelAttribute Products product,
                                 @RequestParam("thumbnail") MultipartFile thumbnail,
                                 @RequestParam("firstImage") MultipartFile firstImage,
                                 @RequestParam("secondImage") MultipartFile secondImage,
                                 @RequestParam("thirdImage") MultipartFile thirdImage,
                                 @ModelAttribute ProductVariation productVariation,
                                 @RequestParam("imageName") String imageName,
                                 HttpSession session, Model model,
                                 RedirectAttributes redirectAttributes) {
        Integer storeId = (Integer) session.getAttribute("storeId");

        // Validate product input
        if (product.getProductName() == null || product.getProductName().isEmpty() || product.getProductName().length() > 100) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên sản phẩm không được để trống và không được vượt quá 100 ký tự và không được để trống.");
            return "redirect:/addProductForm2/" + storeId;
        }
        if (product.getProductDescription() == null || product.getProductDescription().isEmpty() || product.getProductDescription().length() > 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mô tả sản phẩm không được để trống và không được vượt quá 500 ký tự và không được để trống.");
            return "redirect:/addProductForm2/" + storeId;
        }
        if (product.getPrice() == null || product.getPrice() < 20000 || product.getPrice() > 50000000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giá sản phẩm phải lớn hơn hoặc bằng 20,000 và không được vượt quá 50,000,000 và không được để trống.");
            return "redirect:/addProductForm2/" + storeId;
        }
        if (product.getWeight() < 0.1 || product.getWeight() > 20.0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khối lượng sản phẩm phải trong khoảng từ 0.1kg đến 20kg.");
            return "redirect:/addProductForm2/" + storeId;
        }

        if (storeId != null) {
            Optional<Stores> store = storeService.findById(storeId);
            store.ifPresent(product::setStoreId);
        }
        productService.addProduct(product);

        // Validate and add product images
        if (imageName == null || imageName.isEmpty() || imageName.length() > 255) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên hình ảnh không được để trống và không được vượt quá 255 ký tự.");
            return "redirect:/addProductForm2/" + storeId;
        }

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
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải lên hình ảnh. Vui lòng thử lại.");
            return "redirect:/addProductForm2/" + storeId;
        }
        productService.addProductImage(productImage);

        // Validate and add product variation
        if (productVariation.getStock() == null || productVariation.getStock() < 0) {
            redirectAttributes.addFlashAttribute("errorMessageVar", "Số lượng sản phẩm phải lớn hơn hoặc bằng 0.");
            return "redirect:/addProductForm2/" + storeId;
        }
        Products maxProductId = productService.getMaxProductId();
        ProductImage maxImageId = productService.getMaxImageId();
        productVariation.setProductId(maxProductId);
        productVariation.setImageId(maxImageId);
        productService.addProductVariation(productVariation);

        // Repopulate categories and product attributes to retain the filled-in values
        List<Categories> categories = categoriesService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product); // Keep the filled-in product details
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        return "redirect:/addProductForm2/" + storeId;
    }


    @GetMapping("/delete-product/{productId}")
    public String deleteProduct(@PathVariable Integer productId, HttpServletRequest request,
                                HttpSession session) {
        Integer storeId = (Integer) session.getAttribute("storeId");
        productService.deleteProductById(productId);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/seller-products/" + storeId);
    }

    @GetMapping("/addProductVariationForm/{productId}")
    public String showAddProductVariationForm(@PathVariable Integer productId, Model model, HttpSession session) {
        Optional<Products> product = productService.findById(productId);
        List<Size> sizes = sizeService.getAllSizes();
        List<Color> colors = colorService.getAllColors();

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("productId", productId);
            model.addAttribute("sizes", sizes);
            model.addAttribute("colors", colors);
            model.addAttribute("productImage", new ProductImage());
            model.addAttribute("productVariation", new ProductVariation());
            return "seller/add-variation";
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/seller-products/" + session.getAttribute("storeId");
        }
    }

    @PostMapping("/addProductVariation")
    public String addProductVariation(@RequestParam Integer productId,
                                      @ModelAttribute ProductVariation productVariation,
                                      @RequestParam("thumbnail") MultipartFile thumbnail,
                                      @RequestParam("firstImage") MultipartFile firstImage,
                                      @RequestParam("secondImage") MultipartFile secondImage,
                                      @RequestParam("thirdImage") MultipartFile thirdImage,
                                      @RequestParam("imageName") String imageName,
                                      RedirectAttributes redirectAttributes, HttpSession session) {
        System.out.println("Starting addProductVariation method");
        Optional<Products> product = productService.findById(productId);
        if (product.isEmpty()) {
            System.out.println("Product not found");
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
            return "redirect:/seller-products/" + session.getAttribute("storeId");
        }

        productVariation.setProductId(product.get());
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
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tải lên hình ảnh. Vui lòng thử lại.");
            return "redirect:/addProductVariationForm/" + productId;
        }

        productService.addProductImage(productImage);
        ProductImage maxImageId = productService.getMaxImageId();
        productVariation.setImageId(maxImageId);
        productService.addProductVariation(productVariation);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm biến thể sản phẩm thành công!");
        System.out.println("Product variation added successfully");
        return "redirect:/seller-products";
    }

    @PostMapping("/update-stock")
    public String updateStock(@RequestParam("variationId") Integer variationId,
                              @RequestParam("newStock") Integer newStock,
                              RedirectAttributes redirectAttributes) {
        ProductVariation variation = productService.findProductVariationById(variationId);

        // Validate newStock
        if (newStock < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng mới không thể âm.");
            return "redirect:/seller-products";
        }

        variation.setStock(newStock);
        productVariationRepository.save(variation);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật số lượng thành công.");

        return "redirect:/seller-products";
    }
}
