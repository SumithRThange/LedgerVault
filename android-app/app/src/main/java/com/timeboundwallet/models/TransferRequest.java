package com.timeboundwallet.models;

public class TransferRequest {
    private Long senderWalletId;
    private Long receiverWalletId;
    private double amount;

    public TransferRequest(Long senderWalletId, Long receiverWalletId, double amount) {
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
    }
}
