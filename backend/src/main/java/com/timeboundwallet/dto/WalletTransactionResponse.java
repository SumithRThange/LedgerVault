package com.timeboundwallet.dto;

import com.timeboundwallet.entity.TransactionStatus;
import com.timeboundwallet.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class WalletTransactionResponse {
    private Long id;
    private Long walletId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate expiryDate;
    private TransactionStatus status;
    private LocalDateTime createdAt;
}
