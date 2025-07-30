package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.dto.HistoricalBalanceDTO;
import com.recargapay.wallet.model.dto.UserDTO;
import com.recargapay.wallet.model.dto.WalletOperTransferDTO;
import com.recargapay.wallet.model.dto.WalletOperationDTO;
import com.recargapay.wallet.service.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WalletControllerTest {

    AutoCloseable openMocks;

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void createUser() {
        UserDTO input = UserDTO.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(100.0)).build();
        UserDTO expected = UserDTO.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(100.0)).build();

        when(walletService.createUser(any(), any())).thenReturn(expected);

        UserDTO result = walletController.createUser(input);
        assertEquals(expected, result);
    }

    @Test
    void getUser() {
        UserDTO expected = UserDTO.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(100.0)).build();

        when(walletService.getUser(1L)).thenReturn(expected);

        UserDTO result = walletController.getUser(1L);
        assertEquals(expected, result);
    }

    @Test
    void deposit() {
        WalletOperationDTO input = new WalletOperationDTO("op1", BigDecimal.valueOf(50.0), 1L);
        UserDTO expected = UserDTO.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(150.0)).build();

        when(walletService.deposit(any())).thenReturn(expected);

        UserDTO result = walletController.deposit(input);
        assertEquals(expected, result);
    }

    @Test
    void withdraw() {
        WalletOperationDTO input = new WalletOperationDTO("op1", BigDecimal.valueOf(50.0), 1L);
        UserDTO expected = UserDTO.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(50.0)).build();

        when(walletService.withdraw(any())).thenReturn(expected);

        UserDTO result = walletController.withdraw(input);
        assertEquals(expected, result);
    }

    @Test
    void transfer() {
        WalletOperTransferDTO input =
                new WalletOperTransferDTO("op1", BigDecimal.valueOf(10.0),1L, 2L);

        doNothing().when(walletService).transfer(any());

        String result = walletController.transfer(input);
        assertEquals("Transfer successful", result);
    }

    @Test
    void getHistoricalBalance() {
        Long userId = 1L;
        LocalDateTime timestamp = LocalDateTime.now();
        HistoricalBalanceDTO expected = new HistoricalBalanceDTO(userId, BigDecimal.valueOf(100.0), timestamp);

        when(walletService.getHistoricalBalance(userId, timestamp)).thenReturn(expected);

        HistoricalBalanceDTO result = walletController.getHistoricalBalance(userId, timestamp);
        assertEquals(expected, result);
    }
}
