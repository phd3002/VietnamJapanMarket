package com.ecommerce.g58.enums;

public enum Role {
    ADMIN(1, "Admin"),
    CUSTOMER(3, "Customer"),
    GUEST(4, "Guest"),
    SELLER(2, "Seller");

    private final int id;
    private final String name;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
