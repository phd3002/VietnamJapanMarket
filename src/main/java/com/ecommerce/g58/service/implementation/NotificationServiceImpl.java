package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.NotificationRepository;
import com.ecommerce.g58.service.NotificationService;
import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserService userService;

    @Override
    public List<Notification> getNotifications() {
        String currentUsername = getCurrentUsername();
        if ("anonymousUser".equals(currentUsername)) {
            return List.of(); // Return empty list for anonymous users
        }

        Users currentUser = userService.findByEmail(currentUsername);
        if (currentUser == null) {
            return List.of();
        }
        return notificationRepository.findTop3ByUserIdOrderByCreatedDesc(currentUser.getUserId());
    }

    @Override
    public List<Notification> getTop3() {
        String currentUsername = getCurrentUsername();
        if ("anonymousUser".equals(currentUsername)) {
            return List.of(); // Return empty list for anonymous users
        }

        Users currentUser = userService.findByEmail(currentUsername);
        if (currentUser == null) {
            return List.of();
        }
        return notificationRepository.findTop3ByUserIdOrderByCreatedDesc(currentUser.getUserId());
    }

    @Override
    public Integer getUnreadNotificationCount() {
        String currentUsername = getCurrentUsername();
        if ("anonymousUser".equals(currentUsername)) {
            return 0; // Return 0 for anonymous users
        }

        Users currentUser = userService.findByEmail(currentUsername);
        if (currentUser == null) {
            return 0;
        }
        return notificationRepository.countByUserId_UserIdAndReadFalse(currentUser.getUserId());
    }

    @Override
    public String markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notification ID"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return notification.getUrl();
    }

    @Override
    public void updateNotification(Notification notification) {
        notification.setRead(false);
        notification.setCreated(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
