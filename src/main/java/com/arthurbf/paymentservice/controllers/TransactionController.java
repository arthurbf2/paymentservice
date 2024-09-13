package com.arthurbf.paymentservice.controllers;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.services.TransactionService;
import com.arthurbf.paymentservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionModel> transaction(@RequestBody @Valid TransactionRecordDto transactionRecordDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionRecordDto));
    }
    @GetMapping("/users/{id}/transactions")
    public ResponseEntity<Object> getAllUserTransactions(@PathVariable(value="id") UUID id) {
        userService.validateUser(id);
        List<TransactionModel> transactions = transactionService.getUserTransactions(id);
        if (transactions.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        for (TransactionModel transaction : transactions) {
            UUID transacId = transaction.getId();
            transaction.add(linkTo(methodOn(TransactionController.class).getOneTransaction(id, transacId)).withSelfRel());
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
