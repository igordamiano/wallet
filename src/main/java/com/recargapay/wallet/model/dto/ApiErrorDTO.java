package com.recargapay.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorDTO {
    private String error;
    private String path;
    private LocalDateTime timestamp;
    private String traceId;
}
