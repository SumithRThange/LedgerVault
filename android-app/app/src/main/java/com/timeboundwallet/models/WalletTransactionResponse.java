package com.timeboundwallet.models;

public class WalletTransactionResponse {
    private Long id;
    private Long walletId;
    private String type;
    private double amount;
    private String expiryDate;
    private String status;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
