package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
@ControllerAdvice
public class GlobalNotificationAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("unreadCount")
    public Integer getUnreadCount() {
        try {
            Integer unreadCount = notificationService.getUnreadNotificationCount();
            System.out.println("Unread notifications count: " + unreadCount);
            return unreadCount;
        } catch (Exception e) {
            System.err.println("Failed to fetch unread notifications: " + e.getMessage());
            return 0; // Default to 0 if an error occurs
        }
    }


    @ModelAttribute("topNotifications")
    public List<Notification> getTopNotifications() {
        return notificationService.getTop3();
    }
}
