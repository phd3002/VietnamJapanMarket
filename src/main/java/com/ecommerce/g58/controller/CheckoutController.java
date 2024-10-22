package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Cart;
import com.ecommerce.g58.entity.CartItem;
import com.ecommerce.g58.entity.ProductImage;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.service.CartService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductImageRepository productImageRepository; // Inject ProductImageRepository

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        // lay thong tin user dang dang nhap
        if (principal == null) {
            return "redirect:/sign-in";
        }

        // neu user chua login thi chuyen huong den trang dang nhap
        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        // lay id, gio hang cua user, va danh sach cac mon hang trong cua hang
        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();

        // vong lap de tinh tong gia tri cua gio hang
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            double itemPrice = item.getPrice();
            int quantity = item.getQuantity();
            totalPrice += itemPrice * quantity; // tong gia tri moi = tong gia tri cu + gia tri moi
        }

        // them cac thuoc tinh vao model de hien thi tren trang checkout
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        // lay thong tin anh cua san pham
        Map<Integer, String> productImages = new HashMap<>();
        for (CartItem item : cartItems) {
            List<ProductImage> images = productImageRepository.findByProductProductId(item.getProductId().getProductId());
            if (!images.isEmpty()) {
                productImages.put(item.getProductId().getProductId(), images.get(0).getThumbnail()); // Assuming thumbnail is used
            }
        }
        model.addAttribute("productImages", productImages);

        return "checkout";
    }
}