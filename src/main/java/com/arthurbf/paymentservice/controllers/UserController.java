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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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


}
