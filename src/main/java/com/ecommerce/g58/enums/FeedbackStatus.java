package com.ecommerce.g58.enums;

public enum FeedbackStatus {
    PENDING("Chờ phê duyệt") ,
    COMPLETE("Phê Duyệt Thành Công"),
    REJECT("Từ Chối"),
    VIEWABLE("CHỈ XEM");


    private final String displayName;

    FeedbackStatus(String displayName) {
        this.displayName = displayName;
    }
}
