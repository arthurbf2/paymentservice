package com.arthurbf.paymentservice.controllers;

import com.arthurbf.paymentservice.dtos.UserRecordDto;
import com.arthurbf.paymentservice.exceptions.UserAlreadyExistsException;
import com.arthurbf.paymentservice.models.UserModel;
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserModel> saveUser(@RequestBody UserRecordDto userRecordDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRecordDto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getUsers() {
        List<UserModel> users = userService.getUsers();
        for (UserModel user : users) {
            UUID id = user.getId();
            user.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable UUID id) {
        userService.validateUser(id);
        Optional<UserModel> user = userService.getUser(id);
        user.get().add(linkTo(methodOn(UserController.class).getUsers()).withSelfRel());
        user.get().add(linkTo(methodOn(TransactionController.class).getAllUserTransactions(id)).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


}
