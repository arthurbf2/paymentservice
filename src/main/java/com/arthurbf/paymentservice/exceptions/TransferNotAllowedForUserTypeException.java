package com.arthurbf.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TransferNotAllowedForUserTypeException extends RuntimeException {
    public ProblemDetail toProblemDetail() {
        var x = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        x.setTitle("This type of user is not allowed to do transfering operations");
        return x;
    }
}
