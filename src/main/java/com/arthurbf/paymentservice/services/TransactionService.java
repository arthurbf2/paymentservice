package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.exceptions.InsufficientBalanceException;
import com.arthurbf.paymentservice.exceptions.SelfTransferException;
import com.arthurbf.paymentservice.exceptions.TransferNotAllowedForUserTypeException;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.TransactionRepository;
import com.arthurbf.paymentservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionModel createTransaction(TransactionRecordDto transactionRecordDto) {
        var sender = userRepository.findById(transactionRecordDto.senderId())
                .orElseThrow(() -> new RuntimeException("Paying user not found!"));
        var receiver = userRepository.findById(transactionRecordDto.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiving user not found!"));
        validateTransaction(transactionRecordDto, sender, receiver);
        sender.debit(transactionRecordDto.amount());
        receiver.credit(transactionRecordDto.amount());

        var transaction = new TransactionModel();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(transactionRecordDto.amount());
        transaction.setStatus(TransactionModel.Status.SUCCESS); //TODO: check for authorization
        transaction.setTransactionDate(LocalDateTime.now());

        sender.getSentTransactions().add(transaction);
        receiver.getReceivedTransactions().add(transaction);
        userRepository.save(sender);
        userRepository.save(receiver);
        // send notification
        return transactionRepository.save(transaction);
    }

    private void validateTransaction(TransactionRecordDto transactionRecordDto, UserModel sender, UserModel receiver) {
        if (sender.equals(receiver)) {
            throw new SelfTransferException();
        }
        if (!sender.isBalancerEqualOrGreatherThan(transactionRecordDto.amount())) {
            throw new InsufficientBalanceException();
        }
        if (!sender.isTransferAllowedForUser()){
            throw new TransferNotAllowedForUserTypeException();
        }
    }
}
