package com.recargapay.wallet.service;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.model.WalletOperation;
import com.recargapay.wallet.model.dto.HistoricalBalanceDTO;
import com.recargapay.wallet.model.dto.UserDTO;
import com.recargapay.wallet.model.dto.WalletOperTransferDTO;
import com.recargapay.wallet.model.dto.WalletOperationDTO;
import com.recargapay.wallet.model.enums.OperationType;
import com.recargapay.wallet.model.mapper.UserMapper;
import com.recargapay.wallet.repository.UserRepository;
import com.recargapay.wallet.repository.WalletOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletOperationRepository operationRepository;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void createUser_success() {
        Long id = 1L;
        String name = "Test";
        BigDecimal balance = BigDecimal.TEN;

        when(userRepository.existsById(id)).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserDto(any())).thenReturn(new UserDTO(id, name, balance));

        UserDTO result = walletService.createUser(name, balance);

        assertEquals(id, result.getId());
    }

    @Test
    void createUser_alreadyExists() {
        when(userRepository.findByName("Test")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> walletService.createUser("Test", BigDecimal.TEN));
    }

    @Test
    void getUser_success() {
        User user = new User(1L, "Test", BigDecimal.TEN, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(new UserDTO(1L, "Test", BigDecimal.TEN));

        UserDTO result = walletService.getUser(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> walletService.getUser(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found", ex.getReason());

    }

    @Test
    void deposit_success() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.TEN, 1L);
        User user = new User(1L, "Test", BigDecimal.ZERO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());
        when(userMapper.toUserDto(user)).thenReturn(new UserDTO(1L, "Test", BigDecimal.TEN));

        UserDTO result = walletService.deposit(dto);

        assertEquals(BigDecimal.TEN, result.getBalance());
    }

    @Test
    void deposit_negativeAmount() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.valueOf(-1), 1L);
        User user = new User(1L, "Test", BigDecimal.ZERO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> walletService.deposit(dto));
    }

    @Test
    void deposit_duplicateOperation() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.TEN, 1L);
        User user = new User(1L, "Test", BigDecimal.ZERO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.of(new WalletOperation()));

        assertThrows(IllegalStateException.class, () -> walletService.deposit(dto));
    }

    @Test
    void deposit_optimisticLockingFailure() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.TEN, 1L);
        User user = new User(1L, "Test", BigDecimal.ZERO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());

        when(userRepository.save(any())).thenThrow(new OptimisticLockingFailureException("Simulated"));

        assertThrows(IllegalStateException.class, () -> walletService.deposit(dto));
    }

    @Test
    void withdraw_success() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.ONE, 1L);
        User user = new User(1L, "Test", BigDecimal.TEN, 1L);

        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(new UserDTO(1L, "Test", BigDecimal.valueOf(9)));

        UserDTO result = walletService.withdraw(dto);

        assertEquals(BigDecimal.valueOf(9), result.getBalance());
    }

    @Test
    void withdraw_insufficientFunds() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.TEN, 1L);
        User user = new User(1L, "Test", BigDecimal.ONE, 1L);

        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> walletService.withdraw(dto));
    }

    @Test
    void withdraw_optimisticLockingFailure() {
        WalletOperationDTO dto = new WalletOperationDTO("op1", BigDecimal.ONE, 1L);
        User user = new User(1L, "Test", BigDecimal.TEN, 1L);

        when(operationRepository.findByOperationId("op1")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.save(any())).thenThrow(new OptimisticLockingFailureException("Simulated"));

        assertThrows(IllegalStateException.class, () -> walletService.withdraw(dto));
    }

    @Test
    void transfer_success() {
        WalletOperTransferDTO dto =
                new WalletOperTransferDTO("op1", BigDecimal.ONE, 1L, 2L);
        User sender = new User(1L, "Sender", BigDecimal.TEN, 1L);
        User receiver = new User(2L, "Receiver", BigDecimal.ZERO, 1L);

        when(operationRepository.findByOperationId("op1_withdraw")).thenReturn(Optional.empty());
        when(operationRepository.findByOperationId("op1_deposit")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertDoesNotThrow(() -> walletService.transfer(dto));
    }

    @Test
    void transfer_sameUser() {
        WalletOperTransferDTO dto =
                new WalletOperTransferDTO("op1", BigDecimal.ONE,1L, 1L);

        when(operationRepository.findByOperationId("op1_withdraw")).thenReturn(Optional.empty());
        when(operationRepository.findByOperationId("op1_deposit")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(dto));
    }

    @Test
    void transfer_optimisticLockingFailure() {
        WalletOperTransferDTO dto =
                new WalletOperTransferDTO("op1", BigDecimal.ONE,1L, 2L);
        User sender = new User(1L, "Sender", BigDecimal.TEN, 1L);
        User receiver = new User(2L, "Receiver", BigDecimal.ZERO, 1L);

        when(operationRepository.findByOperationId("op1_withdraw")).thenReturn(Optional.empty());
        when(operationRepository.findByOperationId("op1_deposit")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(userRepository.save(any())).thenThrow(new OptimisticLockingFailureException("Simulated"));

        assertThrows(IllegalStateException.class, () -> walletService.transfer(dto));
    }

    @Test
    void getHistoricalBalance_success() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        User user = new User(userId, "Test", BigDecimal.TEN, 1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(operationRepository.sumByUserIdAndTypeAndTimestampBefore(userId, OperationType.DEPOSIT.name(), now))
                .thenReturn(Optional.of(BigDecimal.TEN));
        when(operationRepository.sumByUserIdAndTypeAndTimestampBefore(userId, OperationType.WITHDRAW.name(), now))
                .thenReturn(Optional.of(BigDecimal.ONE));
        when(operationRepository.sumByUserIdAndTypeAndTimestampBefore(userId, OperationType.TRANSFER.name() + "_withdraw", now))
                .thenReturn(Optional.of(BigDecimal.ONE));
        when(operationRepository.sumByUserIdAndTypeAndTimestampBefore(userId, OperationType.TRANSFER.name() + "_deposit", now))
                .thenReturn(Optional.of(BigDecimal.ONE));

        HistoricalBalanceDTO dto = walletService.getHistoricalBalance(userId, now);
        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE).subtract(BigDecimal.ONE).subtract(BigDecimal.ONE), dto.getBalance());
    }

    @Test
    void getHistoricalBalance_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getHistoricalBalance(1L, LocalDateTime.now()));
    }
}
