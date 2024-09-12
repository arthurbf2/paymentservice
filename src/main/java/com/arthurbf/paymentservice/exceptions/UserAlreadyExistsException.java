package com.arthurbf.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UserAlreadyExistsException extends RuntimeException{

    private final String cpfcnpj;
    public UserAlreadyExistsException(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }
    public ProblemDetail toProblemDetail() {
        var x = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        x.setTitle("There already exists a user with CPF/CPNPJ " + cpfcnpj);
        return x;
    }
}
