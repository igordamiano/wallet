package com.recargapay.wallet.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletOperTransferDTO {

    // Unique key for idempotency
    protected String operationId;

    protected BigDecimal amount;

    protected Long senderUserId;

    // For transfers
    protected Long receiverUserId;

}
