package com.ecommerce.g58.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    PROCESSING("Đang gói hàng"),
    DISPATCHED("Đã giao cho đơn vị vận chuyển"),
    SHIPPING("Đang giao"),
    FAILED("Giao hàng thất bại"),
    DELIVERED("Đã giao hàng"),
    COMPLETE("Hoàn thành"),
    RETURNED("Hoàn Tiền"),
    CANCELLED("Đã hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
