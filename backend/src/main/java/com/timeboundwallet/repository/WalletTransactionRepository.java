package com.timeboundwallet.repository;

import com.timeboundwallet.entity.TransactionStatus;
import com.timeboundwallet.entity.TransactionType;
import com.timeboundwallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    List<WalletTransaction> findByTypeAndStatusAndExpiryDateBefore(
            TransactionType type,
            TransactionStatus status,
            LocalDate expiryDate
    );
}
