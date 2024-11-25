package com.ecommerce.g58.service;


import com.ecommerce.g58.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications();

    List<Notification> getTop3();

    Integer getUnreadNotificationCount();

    String markAsRead(Long notificationId);

    void updateNotification(Notification notification) ;
}