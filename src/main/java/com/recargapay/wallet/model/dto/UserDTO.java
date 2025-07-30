package com.recargapay.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private BigDecimal balance;

}
