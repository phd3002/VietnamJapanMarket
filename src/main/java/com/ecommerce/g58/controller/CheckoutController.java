package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.ProductImageRepository;
import com.ecommerce.g58.service.*;
import com.ecommerce.g58.service.implementation.ShippingUnitServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

    @Autowired
    private ShippingRateService shippingRateService;

    @Autowired
    private CountriesService countriesSerive;
    @Autowired
    private ShippingUnitService shippingUnitService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/sign-in";
        }

        String username = principal.getName();
        Users user = userService.findByEmail(username);
        if (user == null) {
            return "redirect:/sign-in";
        }

        Integer userId = user.getUserId();
        Cart userCart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = userCart.getCartItems();

        // Calculate total price based on quantity of each cart item
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            double itemPrice = item.getPrice(); // Assuming item.getPrice() returns the price of a single unit
            int quantity = item.getQuantity(); // Assuming item.getQuantity() returns the quantity of the item in the cart
            totalPrice += itemPrice * quantity;
        }
        List<ShippingUnit> shippingUnits = shippingUnitService.getAllShippingUnits();
        List<ShippingRate> shippingRates = shippingRateService.getAllShippingRates();
        model.addAttribute("shippingRates", shippingRates);
        model.addAttribute("countries", countriesSerive.getAllCountries());
        model.addAttribute("users", user);
        model.addAttribute("shippingUnits", shippingUnits);
        // Add cart items and total price to the model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        // Use existing function to get images for each cart item
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