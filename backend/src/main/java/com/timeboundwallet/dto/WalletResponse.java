package com.timeboundwallet.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class WalletResponse {
    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal maxLimit;
}
