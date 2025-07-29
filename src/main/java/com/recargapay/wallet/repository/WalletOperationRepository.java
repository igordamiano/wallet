package com.recargapay.wallet.repository;

import com.recargapay.wallet.model.WalletOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletOperationRepository extends JpaRepository<WalletOperation, Long> {

    Optional<WalletOperation> findByOperationId(String operationId);

}