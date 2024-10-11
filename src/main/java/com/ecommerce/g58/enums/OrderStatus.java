package com.ecommerce.g58.enums;

public enum OrderStatus {
    PENDING("Chờ xử lý"),
    PROGRESS("Đang giao"),
    COMPLETE("Hoàn thành"),
    REFUND("Hoàn Tiền"),
    REJECT("Từ chối");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
