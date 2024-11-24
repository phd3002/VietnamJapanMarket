package com.ecommerce.g58.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;
    private String title;
    private String content;
    private LocalDateTime created;

    @Column(name = "is_read")
    private boolean read;
    private String url;
}
