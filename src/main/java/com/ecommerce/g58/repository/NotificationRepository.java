package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Notification;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserId_UserIdOrderByCreatedDesc(int id);
    @Query(value = "SELECT * FROM notification n WHERE n.user_id = :userId ORDER BY n.created DESC", nativeQuery = true)
    Page<Notification> findTop3ByUserIdOrderByCreatedDesc(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT * FROM notification n WHERE n.user_id = :userId ORDER BY n.created DESC LIMIT 3", nativeQuery = true)
    List<Notification> findTop3ByUserIdOrderByCreatedDescending(@Param("userId") int userId);
    Integer countByUserId_UserIdAndReadFalse(int userId);

}
