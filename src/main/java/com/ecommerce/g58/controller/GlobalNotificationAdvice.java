package com.ecommerce.g58.controller;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            return notificationService.getUnreadNotificationCount();
        } catch (Exception e) {
            return 0;
        }
    }


    @ModelAttribute("topNotifications")
    public List<Notification> getTopNotifications() {
        return notificationService.getTop3();
    }
}
