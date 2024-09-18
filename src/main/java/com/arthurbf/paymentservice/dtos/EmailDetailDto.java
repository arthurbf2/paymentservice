package com.arthurbf.paymentservice.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDetailDto(
        @Email @NotBlank String emailFrom,
        @NotBlank String subject,
        @Email @NotBlank String emailTo,
        @NotBlank String body) {
}
