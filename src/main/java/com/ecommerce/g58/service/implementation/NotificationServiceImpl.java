package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Notification;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.repository.NotificationRepository;
import com.ecommerce.g58.service.NotificationService;
import com.ecommerce.g58.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
        return notificationRepository.findTop3ByUserIdOrderByCreatedDescending(currentUser.getUserId());
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

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "anonymousUser";
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            String roles = userDetails.getAuthorities().toString();
//            logger.info("User: {}, Roles: {}", username, roles);
            return username;
        } else {
            String principal = authentication.getPrincipal().toString();
//            logger.info("User: {}, Roles: {}", principal, "N/A");
            return principal;
        }
    }
}
