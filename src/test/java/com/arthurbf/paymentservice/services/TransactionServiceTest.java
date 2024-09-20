package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.exceptions.InsufficientBalanceException;
import com.arthurbf.paymentservice.exceptions.SelfTransferException;
import com.arthurbf.paymentservice.exceptions.TransferNotAllowedForUserTypeException;
import com.arthurbf.paymentservice.exceptions.UnauthorizedTransactionException;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private UserService userService;
    @Mock
    private EmailService emailService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    private UserModel createUser(BigDecimal balance, UserModel.UserType type) {
        var user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setBalance(balance);
        user.setUserType(type);
        return user;
    }

    private TransactionRecordDto createTransactionDto(BigDecimal amount, UUID senderId, UUID receiverId) {
        return new TransactionRecordDto(amount, senderId, receiverId, LocalDateTime.now(), TransactionModel.Status.PENDING);
    }

    @Test
    void testCreateSuccessfulTransaction() {
        var sender = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);
        var receiver = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);

        when(userService.getUser(sender.getId())).thenReturn(Optional.of(sender));
        when(userService.getUser(receiver.getId())).thenReturn(Optional.of(receiver));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        var transactionDto = createTransactionDto(new BigDecimal(10), sender.getId(), receiver.getId());
        transactionService.createTransaction(transactionDto);

        ArgumentCaptor<TransactionModel> transactionCaptor = ArgumentCaptor.forClass(TransactionModel.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        TransactionModel createdTransaction = transactionCaptor.getValue();

        assertEquals(sender, createdTransaction.getSender());
        assertEquals(receiver, createdTransaction.getReceiver());
        assertTrue(sender.getSentTransactions().contains(createdTransaction));
        assertTrue(receiver.getReceivedTransactions().contains(createdTransaction));

        ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(userService, times(2)).saveUser(userCaptor.capture());
        UserModel updatedSender = userCaptor.getAllValues().get(0);
        UserModel updatedReceiver = userCaptor.getAllValues().get(1);
        assertEquals(new BigDecimal(0), updatedSender.getBalance());
        assertEquals(new BigDecimal(20), updatedReceiver.getBalance());
    }

    @Test
    void testCreateUnauthorizedTransaction() throws UnauthorizedTransactionException {
        var sender = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);
        var receiver = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);

        when(userService.getUser(sender.getId())).thenReturn(Optional.of(sender));
        when(userService.getUser(receiver.getId())).thenReturn(Optional.of(receiver));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        UnauthorizedTransactionException exception = assertThrows(UnauthorizedTransactionException.class, () -> {
            var transactionDto = createTransactionDto(new BigDecimal(10), sender.getId(), receiver.getId());
            transactionService.createTransaction(transactionDto);
        });
        assertEquals(exception.getMessage(), "Operation unauthorized.");
    }

    @Test
    void testCreateUnsuccessfulTransaction1() throws InsufficientBalanceException {
        var sender = createUser(new BigDecimal(0), UserModel.UserType.CUSTOMER);
        var receiver = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);

        when(userService.getUser(sender.getId())).thenReturn(Optional.of(sender));
        when(userService.getUser(receiver.getId())).thenReturn(Optional.of(receiver));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            var transactionDto = createTransactionDto(new BigDecimal(10), sender.getId(), receiver.getId());
            transactionService.createTransaction(transactionDto);
        });
        assertEquals(exception.getMessage(), "Insufficient balance");
    }

    @Test
    void testCreateInvalidTransaction1() throws SelfTransferException {
        var sender = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);
        when(userService.getUser(sender.getId())).thenReturn(Optional.of(sender));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        SelfTransferException exception = assertThrows(SelfTransferException.class, () -> {
            var transactionDto = createTransactionDto(new BigDecimal(10), sender.getId(), sender.getId());
            transactionService.createTransaction(transactionDto);
        });
        assertEquals(exception.getMessage(), "You cannot transfer to yourself.");
    }

    @Test
    void testCreateInvalidTransaction2() throws TransferNotAllowedForUserTypeException {
        var sender = createUser(new BigDecimal(15), UserModel.UserType.MERCHANT);
        var receiver = createUser(new BigDecimal(10), UserModel.UserType.CUSTOMER);
        sender.setBalance(new BigDecimal(15));
        receiver.setBalance(new BigDecimal(10));

        when(userService.getUser(sender.getId())).thenReturn(Optional.of(sender));
        when(userService.getUser(receiver.getId())).thenReturn(Optional.of(receiver));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransferNotAllowedForUserTypeException exception = assertThrows(TransferNotAllowedForUserTypeException.class, () -> {
            var transactionDto = createTransactionDto(new BigDecimal(10), sender.getId(), receiver.getId());
            transactionService.createTransaction(transactionDto);
        });
        assertEquals(exception.getMessage(), "This type of user is not allowed to do transfering operations");
    }
}
