package com.recargapay.wallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    protected Long id;

    // Unique key for idempotency
    @Column(unique = true, nullable = false)
    protected String operationId;

    protected Long userId;

    protected BigDecimal amount;

    // deposit, withdraw, transfer
    protected String type;

}