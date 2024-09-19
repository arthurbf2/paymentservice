package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.EmailDetailDto;
import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.exceptions.*;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.TransactionRepository;
import com.arthurbf.paymentservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authService;
    private final EmailService emailService;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AuthorizationService authService, EmailService emailService, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    public TransactionModel createTransaction(TransactionRecordDto transactionRecordDto) {
        var sender = userService.getUser(transactionRecordDto.senderId())
                .orElseThrow(UserNotFoundException::new);
        var receiver = userService.getUser(transactionRecordDto.receiverId())
                .orElseThrow(UserNotFoundException::new);
        validateTransaction(transactionRecordDto, sender, receiver);
        if (!authService.authorizeTransaction(sender, transactionRecordDto.amount()))
            throw new UnauthorizedTransactionException();
        sender.debit(transactionRecordDto.amount());
        receiver.credit(transactionRecordDto.amount());

        var transaction = new TransactionModel();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(transactionRecordDto.amount());
        transaction.setStatus(TransactionModel.Status.SUCCESS);
        transaction.setTransactionDate(LocalDateTime.now());

        sender.getSentTransactions().add(transaction);
        receiver.getReceivedTransactions().add(transaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
        emailService.sendEmailNotification(sender, receiver, transactionRecordDto.amount());
        return transactionRepository.save(transaction);
    }

    private void validateTransaction(TransactionRecordDto transactionRecordDto, UserModel sender, UserModel receiver) {

        if (sender.getId().equals(receiver.getId())) {
            throw new SelfTransferException();
        }
        if (!sender.isBalancerEqualOrGreatherThan(transactionRecordDto.amount())) {
            throw new InsufficientBalanceException();
        }
        if (!sender.isTransferAllowedForUser()){
            throw new TransferNotAllowedForUserTypeException();
        }
    }

    public List<TransactionModel> getUserTransactions(UUID userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    public List<TransactionModel> getSentTransactions(UUID userId) {
        return transactionRepository.findAllSentByUserId(userId);
    }

    public List<TransactionModel> getReceivedTransactions(UUID userId) {
        return transactionRepository.findAllReceivedByUserId(userId);
    }

    public Optional<TransactionModel> getTransaction(UUID id) {
        return transactionRepository.findById(id);
    }
}
