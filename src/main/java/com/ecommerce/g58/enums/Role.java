package com.ecommerce.g58.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN(1, "Admin"),
    CUSTOMER(3, "Customer"),
    GUEST(4, "Guest"),
    SELLER(2, "Seller"),
    LOGISTIC(5, "Logistic");

    private final int id;
    private final String name;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
