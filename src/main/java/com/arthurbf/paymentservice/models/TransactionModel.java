package com.arthurbf.paymentservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_TRANSACTIONS")
public class TransactionModel extends RepresentationModel<TransactionModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transaction_id;

    @NotNull
    @Positive
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserModel sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserModel receiver;

    @NotNull
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SUCCESS,
        FAILED,
        PENDING
    }

    public UUID getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(UUID transaction_id) {
        this.transaction_id = transaction_id;
    }

    public @NotNull @Positive BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @Positive BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull UserModel getSender() {
        return sender;
    }

    public void setSender(@NotNull UserModel sender) {
        this.sender = sender;
    }

    public @NotNull UserModel getReceiver() {
        return receiver;
    }

    public void setReceiver(@NotNull UserModel receiver) {
        this.receiver = receiver;
    }

    public @NotNull LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(@NotNull LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
