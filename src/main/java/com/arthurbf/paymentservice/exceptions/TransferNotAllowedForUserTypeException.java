package com.arthurbf.paymentservice.exceptions;


public class TransferNotAllowedForUserTypeException extends RuntimeException {
    public TransferNotAllowedForUserTypeException() {
        super("This type of user is not allowed to do transfering operations");
    }
}
