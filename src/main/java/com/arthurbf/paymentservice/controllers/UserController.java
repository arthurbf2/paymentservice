package com.arthurbf.paymentservice.controllers;

import com.arthurbf.paymentservice.dtos.UserRecordDto;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.services.TransactionService;
import com.arthurbf.paymentservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {

    private final TransactionService transactionService;
    private final UserService userService;

    public UserController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserModel> saveUser(@RequestBody UserRecordDto userRecordDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userRecordDto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @GetMapping("/users/{id}/transactions")
    public ResponseEntity<Object> getAllUserTransactions(@PathVariable(value="id") UUID id) {
        userService.validateUser(id);
        List<TransactionModel> transactions = transactionService.getUserTransactions(id);
        if (transactions.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @GetMapping("/users/{userId}/transactions/{transaction_id}")
    public ResponseEntity<Object> getOneTransaction(@PathVariable UUID userId, @PathVariable UUID transaction_id) {
        userService.validateUser(userId);
        Optional<TransactionModel> transaction = transactionService.getTransaction(transaction_id);
        if (transaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

    @GetMapping("/users/{userId}/transactions/sent")
    public ResponseEntity<List<TransactionModel>> getSentTransactions(@PathVariable UUID userId) {
        userService.validateUser(userId);
        List<TransactionModel> transactions = transactionService.getSentTransactions(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }
    @GetMapping("/users/{userId}/transactions/received")
    public ResponseEntity<List<TransactionModel>> getReceivedTransactions(@PathVariable UUID userId) {
        userService.validateUser(userId);
        List<TransactionModel> transactions = transactionService.getReceivedTransactions(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }
}
