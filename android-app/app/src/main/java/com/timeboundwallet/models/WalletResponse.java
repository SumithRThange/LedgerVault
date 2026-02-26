package com.timeboundwallet.models;

public class WalletResponse {
    private Long walletId;
    private Long userId;
    private double balance;
    private double maxLimit;

    public Long getWalletId() {
        return walletId;
    }

    public Long getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public double getMaxLimit() {
        return maxLimit;
    }
}
