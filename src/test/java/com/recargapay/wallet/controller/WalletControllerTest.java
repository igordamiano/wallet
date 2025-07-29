package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.dto.UserDto;
import com.recargapay.wallet.service.WalletService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        //Given
        UserDto input = UserDto.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(100.0)).build();
        UserDto expected = UserDto.builder().id(1L).name("Fulano").balance(BigDecimal.valueOf(100.0)).build();

        //When
        when(walletService.createUser(any(), anyString(), any()))
                .thenReturn(expected);

        //Then
        UserDto result = walletController.createUser(input);
        assertEquals(expected, result);
    }


}