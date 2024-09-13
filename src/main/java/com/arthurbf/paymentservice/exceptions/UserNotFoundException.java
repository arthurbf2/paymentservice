package com.arthurbf.paymentservice.exceptions;


public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("The user you searched for does not exist.");
    }
}
