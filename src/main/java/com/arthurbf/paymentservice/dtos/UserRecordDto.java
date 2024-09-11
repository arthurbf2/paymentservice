package com.arthurbf.paymentservice.dtos;

import com.arthurbf.paymentservice.models.UserModel;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UserRecordDto(
        String name,
        String email,
        BigDecimal balance,
        String password,
        String cpfcnpj,
        UserModel.UserType userType,
        Set<UUID> sentTransactionIds,
        Set<UUID> receivedTransactionIds) {
}
