package com.timeboundwallet.models;

public class WalletCreateRequest {
    private Long userId;
    private double maxLimit;

    public WalletCreateRequest(Long userId, double maxLimit) {
        this.userId = userId;
        this.maxLimit = maxLimit;
    }
}
