package com.arthurbf.paymentservice.exceptions;


public class SelfTransferException extends RuntimeException{
    public SelfTransferException() {
        super("You cannot transfer to yourself.");
    }
}
