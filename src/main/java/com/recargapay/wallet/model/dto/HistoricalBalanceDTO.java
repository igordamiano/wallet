package com.recargapay.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HistoricalBalanceDTO {
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime timestamp;
}
