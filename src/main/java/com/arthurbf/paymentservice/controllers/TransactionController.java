package com.arthurbf.paymentservice.controllers;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.models.TransactionModel;
import com.arthurbf.paymentservice.models.UserModel;
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
            transaction.add(linkTo(methodOn(TransactionController.class).getSentTransactions(id)).withRel("sent transactions"));
            transaction.add(linkTo(methodOn(TransactionController.class).getReceivedTransactions(id)).withRel("received transactions"));
            setSenderReceiverLinks(transaction);
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
        transaction.get().add(linkTo(methodOn(TransactionController.class).getAllUserTransactions(userId)).withRel("all transactions"));
        setSenderReceiverLinks(transaction.get());
        return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

    @GetMapping("/users/{userId}/transactions/sent")
    public ResponseEntity<List<TransactionModel>> getSentTransactions(@PathVariable UUID userId) {
        userService.validateUser(userId);
        List<TransactionModel> transactions = transactionService.getSentTransactions(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        for (TransactionModel transaction : transactions) {
            UUID transacId = transaction.getId();
            transaction.add(linkTo(methodOn(TransactionController.class).getOneTransaction(userId, transacId)).withSelfRel());
            transaction.add(linkTo(methodOn(TransactionController.class).getAllUserTransactions(userId)).withRel("all transactions"));
            transaction.add(linkTo(methodOn(TransactionController.class).getReceivedTransactions(userId)).withRel("received transactions"));
            setSenderReceiverLinks(transaction);
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
        for (TransactionModel transaction : transactions) {
            UUID transacId = transaction.getId();
            transaction.add(linkTo(methodOn(TransactionController.class).getOneTransaction(userId, transacId)).withSelfRel());
            transaction.add(linkTo(methodOn(TransactionController.class).getAllUserTransactions(userId)).withRel("all transactions"));
            transaction.add(linkTo(methodOn(TransactionController.class).getSentTransactions(userId)).withRel("sent transactions"));
            setSenderReceiverLinks(transaction);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    public void setSenderReceiverLinks(TransactionModel transaction) {
        UserModel sender = transaction.getSender();
        UserModel receiver = transaction.getReceiver();
        if (sender.getLinks().isEmpty())
            sender.add(linkTo(methodOn(UserController.class).getUser(sender.getId())).withSelfRel());
        if (receiver.getLinks().isEmpty())
            receiver.add(linkTo(methodOn(UserController.class).getUser(receiver.getId())).withSelfRel());
    }

}
