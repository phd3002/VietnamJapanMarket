package com.ecommerce.g58.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name")
    private String roleName;

//    @ManyToMany(mappedBy = "roles")
//    private Set<Users> users = new HashSet<>();

    public enum RoleName {
        CUSTOMER,
        SELLER,
        GUEST,
        ADMIN
    }
}
