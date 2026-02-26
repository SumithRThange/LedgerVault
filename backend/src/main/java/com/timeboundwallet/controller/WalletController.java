package com.timeboundwallet.controller;

import com.timeboundwallet.dto.*;
import com.timeboundwallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(request));
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<WalletResponse> getWalletBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletBalance(userId));
    }

    @PutMapping("/{userId}/max-limit")
    public ResponseEntity<WalletResponse> updateWalletLimit(@PathVariable Long userId,
                                                            @Valid @RequestBody WalletLimitUpdateRequest request) {
        return ResponseEntity.ok(walletService.updateWalletLimit(userId, request));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<WalletResponse> addMoney(@PathVariable Long userId,
                                                   @Valid @RequestBody AddMoneyRequest request) {
        return ResponseEntity.ok(walletService.addMoney(userId, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getTransactions(walletId));
    }
}
