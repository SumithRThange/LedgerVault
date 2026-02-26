package com.timeboundwallet.service;

import com.timeboundwallet.dto.*;
import com.timeboundwallet.entity.*;
import com.timeboundwallet.exception.BusinessException;
import com.timeboundwallet.exception.ResourceNotFoundException;
import com.timeboundwallet.repository.UserRepository;
import com.timeboundwallet.repository.WalletRepository;
import com.timeboundwallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletResponse createWallet(WalletCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        if (walletRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException("Wallet already exists for this user");
        }

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .maxLimit(request.getMaxLimit())
                .build();

        Wallet saved = walletRepository.save(wallet);
        return toWalletResponse(saved);
    }

    public WalletResponse getWalletBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse updateWalletLimit(Long userId, WalletLimitUpdateRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (request.getMaxLimit().compareTo(wallet.getBalance()) < 0) {
            throw new BusinessException("Max limit cannot be less than current balance");
        }

        wallet.setMaxLimit(request.getMaxLimit());
        walletRepository.save(wallet);
        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse addMoney(Long userId, AddMoneyRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than 0");
        }

        BigDecimal newBalance = wallet.getBalance().add(amount);
        if (newBalance.compareTo(wallet.getMaxLimit()) > 0) {
            throw new BusinessException("Amount cannot be added because wallet limit is breached");
        }

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .amount(amount)
                .expiryDate(LocalDate.now().plusDays(30))
                .status(TransactionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);

        return toWalletResponse(wallet);
    }

    @Transactional
    public ApiResponse transfer(TransferRequest request) {
        Wallet sender = walletRepository.findById(request.getSenderWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found: " + request.getSenderWalletId()));

        Wallet receiver = walletRepository.findById(request.getReceiverWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found: " + request.getReceiverWalletId()));

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than 0");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Sender has insufficient balance");
        }

        BigDecimal receiverNewBalance = receiver.getBalance().add(amount);
        if (receiverNewBalance.compareTo(receiver.getMaxLimit()) > 0) {
            throw new BusinessException("Receiver wallet maxLimit exceeded");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiverNewBalance);
        walletRepository.save(sender);
        walletRepository.save(receiver);

        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(sender)
                .type(TransactionType.DEBIT)
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build());

        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(receiver)
                .type(TransactionType.CREDIT)
                .amount(amount)
                .expiryDate(LocalDate.now().plusDays(30))
                .status(TransactionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        return new ApiResponse("Transfer successful");
    }

    public List<WalletTransactionResponse> getTransactions(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new ResourceNotFoundException("Wallet not found: " + walletId);
        }

        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    // Runs once a day at 1 AM server time.
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void processExpiredCredits() {
        List<WalletTransaction> expiredCredits = walletTransactionRepository
                .findByTypeAndStatusAndExpiryDateBefore(
                        TransactionType.CREDIT,
                        TransactionStatus.ACTIVE,
                        LocalDate.now()
                );

        for (WalletTransaction creditTx : expiredCredits) {
            Wallet wallet = creditTx.getWallet();

            // Mark original credit as expired.
            creditTx.setStatus(TransactionStatus.EXPIRED);
            walletTransactionRepository.save(creditTx);

            // Deduct only if enough balance is still available.
            if (wallet.getBalance().compareTo(creditTx.getAmount()) >= 0) {
                wallet.setBalance(wallet.getBalance().subtract(creditTx.getAmount()));
                walletRepository.save(wallet);
            }

            // Track expiry handling with a REFUND transaction.
            walletTransactionRepository.save(WalletTransaction.builder()
                    .wallet(wallet)
                    .type(TransactionType.REFUND)
                    .amount(creditTx.getAmount())
                    .status(TransactionStatus.REFUNDED)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .maxLimit(wallet.getMaxLimit())
                .build();
    }

    private WalletTransactionResponse toTransactionResponse(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .id(tx.getId())
                .walletId(tx.getWallet().getId())
                .type(tx.getType())
                .amount(tx.getAmount())
                .expiryDate(tx.getExpiryDate())
                .status(tx.getStatus())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
