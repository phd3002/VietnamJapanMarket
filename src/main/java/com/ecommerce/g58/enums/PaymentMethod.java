package com.ecommerce.g58.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    VNPAY(1, "VNPay"),
    CASH(2, "Tiền mặt"),
    WALLET(3, "Ví điện tử");

    private final int value;
    private final String displayName;

    PaymentMethod(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
