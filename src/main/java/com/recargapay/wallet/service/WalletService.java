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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;

    private final WalletOperationRepository operationRepository;

    private final UserMapper userMapper;

    public UserDTO createUser(String name, BigDecimal initialBalance) {
        log.info("TESTE DE TRACE");
        if (userRepository.findByName(name).isPresent()) {
            log.error("User with name {} already exists", name);
            throw new IllegalArgumentException("User already exists");
        }

        BigDecimal balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;

        User user = new User();
        user.setName(name);
        user.setBalance(balance);

        userRepository.save(user);

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            WalletOperation depositOperation = WalletOperation.builder()
                    .operationId(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .amount(balance)
                    .type(OperationType.DEPOSIT.name())
                    .createdAt(LocalDateTime.now())
                    .build();

            operationRepository.save(depositOperation);
        }

        return userMapper.toUserDto(user);
    }


    public UserDTO getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        }
        return userMapper.toUserDto(user.get());
    }

    @Transactional
    public UserDTO deposit(WalletOperationDTO operation) {
        User user = userRepository.findById(operation.getUserId()).orElseThrow();
        try {
            checkDuplicate(operation.getOperationId());
            if (operation.getAmount().compareTo(BigDecimal.ZERO) < 0){
                log.error("Amount must be greater than zero");
                throw new IllegalArgumentException("Amount must be greater than zero");
            }
            user.setBalance(user.getBalance().add(operation.getAmount()));
            userRepository.save(user);
            operationRepository.save(
                    WalletOperation.builder()
                            .operationId(operation.getOperationId())
                            .userId(operation.getUserId())
                            .amount(operation.getAmount())
                            .type(OperationType.DEPOSIT.name())
                            .build());

        } catch (OptimisticLockingFailureException e) {
            logOptimisticLockingFailure(e);
        }
        return userMapper.toUserDto(user);
    }

    @Transactional
    public UserDTO withdraw(WalletOperationDTO operation) {
        checkDuplicate(operation.getOperationId());
        User user = checkAccountBalance(operation.getUserId(), operation.getAmount());
        try {
            userRepository.save(user);
            operationRepository.save(
                    WalletOperation.builder()
                        .operationId(operation.getOperationId())
                        .userId(operation.getUserId())
                        .amount(operation.getAmount())
                        .type(OperationType.WITHDRAW.name())
                        .build());
        } catch (OptimisticLockingFailureException e) {
            logOptimisticLockingFailure(e);
        }
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void transfer(WalletOperTransferDTO operation) {
        checkDuplicate(operation.getOperationId() + "_withdraw");
        checkDuplicate(operation.getOperationId() + "_deposit");
        try {
            if (operation.getSenderUserId().equals(operation.getReceiverUserId())) {
                log.error("Cannot transfer to self: senderId = {}", operation.getSenderUserId());
                throw new IllegalArgumentException("Cannot transfer to self");
            }
            User from = checkAccountBalance(operation.getSenderUserId(), operation.getAmount());
            User to = userRepository.findById(operation.getReceiverUserId()).orElseThrow();
            to.setBalance(to.getBalance().add(operation.getAmount()));
            userRepository.save(from);
            userRepository.save(to);
            operationRepository.save(WalletOperation.builder()
                    .operationId(operation.getOperationId() + "_withdraw")
                    .userId(from.getId())
                    .amount(operation.getAmount())
                    .type(OperationType.TRANSFER.name())
                    .build());
            operationRepository.save(WalletOperation.builder()
                    .operationId(operation.getOperationId() + "_deposit")
                    .userId(to.getId())
                    .amount(operation.getAmount())
                    .type(OperationType.TRANSFER.name())
                    .build());
        } catch (OptimisticLockingFailureException e) {
            logOptimisticLockingFailure(e);
        }
    }

    private User checkAccountBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException("Amount must be positive");
        }
        if (user.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient funds");
        user.setBalance(user.getBalance().subtract(amount));
        return user;
    }

    private void checkDuplicate(String operationId) {
        if (operationRepository.findByOperationId(operationId).isPresent()) {
            throw new IllegalStateException("Duplicated operation");
        }
    }

    private void logOptimisticLockingFailure(OptimisticLockingFailureException e) {
        log.error("Optimistic locking failure: {}", e.getMessage());
        throw new IllegalStateException("Operation failed due to concurrent modification. Retry the operation.");
    }

    public HistoricalBalanceDTO getHistoricalBalance(Long userId, LocalDateTime timestamp) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found"));

        // Sum of deposits up to the timestamp
        BigDecimal deposits = operationRepository.sumByUserIdAndTypeAndTimestampBefore(
                userId, OperationType.DEPOSIT.name(), timestamp).orElse(BigDecimal.ZERO);

        // Sum of withdrawals up to the timestamp
        BigDecimal withdrawals = operationRepository.sumByUserIdAndTypeAndTimestampBefore(
                userId, OperationType.WITHDRAW.name(), timestamp).orElse(BigDecimal.ZERO);

        // Sum of transfers sent up to the timestamp
        BigDecimal sentTransfers = operationRepository.sumByUserIdAndTypeAndTimestampBefore(
                userId, OperationType.TRANSFER.name() + "_withdraw", timestamp).orElse(BigDecimal.ZERO);

        // Sum of transfers received up to the timestamp
        BigDecimal receivedTransfers = operationRepository.sumByUserIdAndTypeAndTimestampBefore(
                userId, OperationType.TRANSFER.name() + "_deposit", timestamp).orElse(BigDecimal.ZERO);

        BigDecimal balance = deposits
                .add(receivedTransfers)
                .subtract(withdrawals)
                .subtract(sentTransfers);

        return new HistoricalBalanceDTO(userId, balance, timestamp);
    }

}