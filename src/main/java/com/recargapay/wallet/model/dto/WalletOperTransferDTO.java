package com.recargapay.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class WalletOperTransferDTO {

    // Unique key for idempotency
    private String operationId;

    private BigDecimal amount;

    private Long senderUserId;

    // For transfers
    private Long receiverUserId;

}
