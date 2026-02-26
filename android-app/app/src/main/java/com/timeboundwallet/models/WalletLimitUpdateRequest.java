package com.timeboundwallet.models;

public class WalletLimitUpdateRequest {
    private double maxLimit;

    public WalletLimitUpdateRequest(double maxLimit) {
        this.maxLimit = maxLimit;
    }
}
