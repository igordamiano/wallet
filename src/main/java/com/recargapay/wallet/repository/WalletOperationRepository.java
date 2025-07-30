package com.recargapay.wallet.repository;

import com.recargapay.wallet.model.WalletOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WalletOperationRepository extends JpaRepository<WalletOperation, Long> {

    Optional<WalletOperation> findByOperationId(String operationId);

    @Query("SELECT SUM(o.amount) FROM WalletOperation o " +
            "WHERE o.userId = :userId AND o.type = :type AND o.createdAt <= :timestamp")
    Optional<BigDecimal> sumByUserIdAndTypeAndTimestampBefore(Long userId, String type, LocalDateTime timestamp);

}