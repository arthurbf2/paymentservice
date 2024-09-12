package com.arthurbf.paymentservice.controllers;

import com.arthurbf.paymentservice.dtos.TransactionRecordDto;
import com.arthurbf.paymentservice.models.TransactionModel;

import com.arthurbf.paymentservice.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionModel> transaction(@RequestBody @Valid TransactionRecordDto transactionRecordDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionRecordDto));
    }

}
