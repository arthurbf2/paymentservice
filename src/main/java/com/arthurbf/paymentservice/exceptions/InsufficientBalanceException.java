package com.arthurbf.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class InsufficientBalanceException extends RuntimeException {
    public ProblemDetail toProblemDetail() {
        var x = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        x.setTitle("Insufficient balance");
        x.setDetail("You cannot transfer a bigger value than your current balance");
        return x;
    }
}