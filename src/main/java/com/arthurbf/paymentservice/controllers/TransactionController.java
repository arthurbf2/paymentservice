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

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionModel> transaction(@RequestBody @Valid TransactionRecordDto transactionRecordDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionRecordDto));
    }
}
