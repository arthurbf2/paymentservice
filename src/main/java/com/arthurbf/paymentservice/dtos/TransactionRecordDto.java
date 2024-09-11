package com.arthurbf.paymentservice.dtos;


import com.arthurbf.paymentservice.models.TransactionModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRecordDto(
    BigDecimal amount,
    UUID senderId,
    UUID receiverId,
    LocalDateTime transactionDate,
    TransactionModel.Status status) {
}
