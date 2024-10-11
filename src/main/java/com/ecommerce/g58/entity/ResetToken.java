package com.ecommerce.g58.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Builder
@AllArgsConstructor
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime expiryDate;

    public ResetToken() {
    }

    public ResetToken(String token, Users user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(24); // Token expires in 24 hours
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Users getUser() {

        return user;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
