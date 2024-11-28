package com.ecommerce.g58.controller.Seller;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.entity.ShippingStatus;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.StoreRepository;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.OrderDetailService;
import com.ecommerce.g58.service.OrderService;
import com.ecommerce.g58.service.ShippingStatusService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderManagementController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShippingStatusService shippingStatusService;

    @Autowired
    private UserService userService;

//    @GetMapping("/seller/order-manager")
//    public String listOrders(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/sign-in";
//        }
//        String email = authentication.getName();
//        Users user = userService.findByEmail(email);
//        Integer userId = user.getUserId();
//        List<OrderManagerDTO> orders = orderService.getOrdersForStore(userId);
//        model.addAttribute("orderss", orders);
//        return "seller/order-manager";
//    }

    @GetMapping("/seller/order-manager")
    public String getOrderManagementPage( Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Ensure the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Users owner = userRepository.findByEmail(authentication.getName());
        Optional<Stores> storeOwner = storeRepository.findByOwnerId(owner);
        List<OrderManagerDTO> orders = orderService.getOrdersByStoreId(storeOwner.get().getStoreId());
        model.addAttribute("orders", orders);
        model.addAttribute("storeId", storeOwner.get().getStoreId());
        return "seller/order-manager2";
    }

    @PostMapping("/seller/update-order-status")
    public String updateOrderStatus(@RequestParam("orderId") Integer orderId, @RequestParam("status") String status, HttpServletRequest request) {
        orderService.updateOrderStatuss(orderId, status);
        System.out.println("Order status updated to " + status);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;  // Redirects to the same URL
    }

    @PostMapping("/seller//return")
    public String returnOrder(
            @RequestParam("orderId") Integer orderId,
            Integer storeId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "Bạn cần đăng nhập để gửi yêu cầu.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/sign-in";
        }

        boolean isOrderUpdated = orderDetailService.refundOrder(orderId);
        if (isOrderUpdated) {
            redirectAttributes.addFlashAttribute("message", "Hoàn trả đơn hàng thành công.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Không thể xử lý yêu cầu. Vui lòng thử lại sau.");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:order-manager/" + storeId;
    }
}
