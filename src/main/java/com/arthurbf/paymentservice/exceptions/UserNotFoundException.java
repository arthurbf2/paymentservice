package com.arthurbf.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UserNotFoundException extends RuntimeException{
    public ProblemDetail toProblemDetail() {
        var x = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        x.setTitle("User not found");
        x.setDetail("The user you searched for does not exist");
        return x;
    }
}
