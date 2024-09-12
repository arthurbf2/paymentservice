package com.arthurbf.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class SelfTransferException extends RuntimeException{
    public ProblemDetail toProblemDetail() {
        var x = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        x.setTitle("You cannot transfer to yourself");
        return x;
    }
}
