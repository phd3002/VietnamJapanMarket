package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    Page<Notification> getNotifications(Pageable pageable);

    List<Notification> getTop3();

    Integer getUnreadNotificationCount();

    String markAsRead(Long notificationId);

    void updateNotification(Notification notification);
}
