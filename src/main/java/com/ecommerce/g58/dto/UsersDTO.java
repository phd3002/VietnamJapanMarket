package com.ecommerce.g58.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class UsersDTO {
    private String username;
    private String password;
}
