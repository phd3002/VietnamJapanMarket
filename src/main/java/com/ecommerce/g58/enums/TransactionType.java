package com.ecommerce.g58.enums;

public enum TransactionType {
    DEPOSIT("Nạp tiền"),
    WITHDRAW("Rút tiền"),
    PAYMENT("Thanh toán"),
    REFUND("Hoàn tiền"),
    SHIPPING_FEE("Phí vận chuyển"),
    MAINTENANCE_FEE("Phí duy trì"),
    COMMISSION("Hoa hồng");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
