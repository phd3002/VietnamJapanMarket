package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification")
    public String getNotification(Model model) {
        List<Notification> notifications = notificationService.getNotifications(); // Fetch all notifications
        model.addAttribute("notifications", notifications);
        return "notification"; // Corresponds to notification.html
    }


    @PostMapping("/notification/read")
    public String markAsRead(@RequestParam("notificationId") Long notificationId) {
        // Update the notification status to "read"
        String url = notificationService.markAsRead(notificationId);
        return "redirect:" + url; // Redirect back to notifications page
    }


}
