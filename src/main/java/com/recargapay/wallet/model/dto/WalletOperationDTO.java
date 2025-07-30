package com.recargapay.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class WalletOperationDTO {

    private String operationId;

    private BigDecimal amount;

    private Long userId;

}
