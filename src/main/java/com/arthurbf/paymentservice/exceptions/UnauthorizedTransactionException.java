package com.arthurbf.paymentservice.exceptions;

public class UnauthorizedTransactionException extends RuntimeException{
    public UnauthorizedTransactionException() {
        super("Operation unauthorized.");
    }
}
