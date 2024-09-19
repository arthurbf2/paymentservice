package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void testCreateSuccessfulTransaction() {
        var sender = new UserModel();
        var receiver = new UserModel();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        sender.setId(senderId);
        receiver.setId(receiverId);
        sender.setBalance(new BigDecimal(10));
        receiver.setBalance(new BigDecimal(10));
        sender.setUserType(UserModel.UserType.CUSTOMER);
        receiver.setUserType(UserModel.UserType.CUSTOMER);

        when(userService.getUser(senderId)).thenReturn(Optional.of(sender));
        when(userService.getUser(receiverId)).thenReturn(Optional.of(receiver));
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        var transactionDto = new TransactionRecordDto(new BigDecimal(10), senderId, receiverId, LocalDateTime.now(), TransactionModel.Status.SUCCESS);
        transactionService.createTransaction(transactionDto);
        ArgumentCaptor<TransactionModel> transactionCaptor = ArgumentCaptor.forClass(TransactionModel.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        assertEquals(sender.getId(), transactionCaptor.getValue().getSender().getId());

        ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(userService, times(2)).saveUser(userCaptor.capture());
        UserModel updatedSender = userCaptor.getAllValues().get(0);
        UserModel updatedReceiver = userCaptor.getAllValues().get(1);
        assertEquals(new BigDecimal(0), updatedSender.getBalance());
        assertEquals(new BigDecimal(20), updatedReceiver.getBalance());
    }

    @Test
    void testCreateUnsuccessfulTransaction() {

    }
}
