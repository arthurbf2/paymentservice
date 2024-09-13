package com.arthurbf.paymentservice.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException() {
        super("There already exists a user with this CPF/email");
    }
}
