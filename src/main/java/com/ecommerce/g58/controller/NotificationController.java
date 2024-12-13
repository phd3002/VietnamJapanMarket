package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification")
    public String getNotification(Model model, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem thông báo");
            return "redirect:/sign-in";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotifications(pageable); // Fetch all notifications
        model.addAttribute("notifications", notifications.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notifications.getTotalPages());
        return "notification"; // Corresponds to notification.html
    }


    @PostMapping("/notification/read")
    public String markAsRead(@RequestParam("notificationId") Long notificationId) {
        // Update the notification status to "read"
        String url = notificationService.markAsRead(notificationId);
        return "redirect:" + url; // Redirect back to notifications page
    }


}
