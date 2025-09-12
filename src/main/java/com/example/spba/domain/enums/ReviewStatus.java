package com.example.spba.domain.enums;

public enum ReviewStatus {
    PENDING(0),
    PASSED(1),
    REJECTED(2);

    private final int code;

    ReviewStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}