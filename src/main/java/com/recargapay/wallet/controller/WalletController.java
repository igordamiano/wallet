package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.dto.HistoricalBalanceDTO;
import com.recargapay.wallet.model.dto.UserDTO;
import com.recargapay.wallet.model.dto.WalletOperTransferDTO;
import com.recargapay.wallet.model.dto.WalletOperationDTO;
import com.recargapay.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
@Tag(
        name = "Wallet",
        description = "Operations for managing user accounts and digital wallet balances"
)
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account in the system.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Forbidden – access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/user")
    public UserDTO createUser(@RequestBody UserDTO dto) {
        return walletService.createUser(dto.getName(), dto.getBalance());
    }


    @Operation(
            summary = "Retrieve user by ID",
            description = "Returns user details for the given user ID.",
            method = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden – access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return walletService.getUser(id);
    }


    @Operation(
            summary = "Deposit funds into user's wallet (idempotent)",
            description = "Adds the specified amount to the user's wallet. The operation is idempotent to avoid duplicate deposits.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden – access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/deposit")
    public UserDTO deposit(@RequestBody WalletOperationDTO operation) {
        return walletService.deposit(operation);
    }


    @Operation(
            summary = "Withdraw funds from user's wallet (idempotent)",
            description = "Subtracts the specified amount from the user's wallet balance. This operation is idempotent to prevent duplicate withdrawals.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds"),
            @ApiResponse(responseCode = "403", description = "Forbidden – access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/withdraw")
    public UserDTO withdraw(@RequestBody WalletOperationDTO operation) {
        return walletService.withdraw(operation);
    }


    @Operation(
            summary = "Transfer funds between users (idempotent)",
            description = "Transfers a specified amount from one user's wallet to another. This operation is idempotent to prevent duplicate transactions.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds"),
            @ApiResponse(responseCode = "403", description = "Forbidden – access denied"),
            @ApiResponse(responseCode = "404", description = "Sender or recipient user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/transfer")
    public String transfer(@RequestBody WalletOperTransferDTO transfer) {
        walletService.transfer(transfer);
        return "Transfer successful";
    }

    @GetMapping("/user/{id}/balance")
    @Operation(
            summary = "Get historical wallet balance",
            description = "Returns the balance of the user's wallet at a specific timestamp"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historical balance returned"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid timestamp format")
    })
    @SecurityRequirement(name = "bearerAuth")
    public HistoricalBalanceDTO getHistoricalBalance(
            @PathVariable Long id,
            @RequestParam("at") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime at) {
        return walletService.getHistoricalBalance(id, at);
    }
}