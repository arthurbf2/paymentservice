package com.arthurbf.paymentservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_USERS")
public class UserModel extends RepresentationModel<UserModel> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    private String name;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotBlank
    private String password;

    @CPF // just taking CPFs for now
    @NotBlank
    @Column(unique = true) // validation at UserService
    private String cpfcnpj;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "sender")
    private Set<TransactionModel> sentTransactions = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "receiver")
    private Set<TransactionModel> receivedTransactions = new HashSet<>();

    public Set<TransactionModel> getReceivedTransactions() {
        return receivedTransactions;
    }

    public boolean isTransferAllowedForUser() {
        return this.userType.equals(UserType.CUSTOMER);
    }

    public boolean isBalancerEqualOrGreatherThan(BigDecimal value) {
        return this.balance.doubleValue() >= value.doubleValue();
    }

    public void debit(BigDecimal value) {
        this.balance = this.balance.subtract(value);
    }

    public void credit(BigDecimal value) {
        this.balance = this.balance.add(value);
    }

    public void setReceivedTransactions(Set<TransactionModel> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }

    public Set<TransactionModel> getSentTransactions() {
        return sentTransactions;
    }

    public void setSentTransactions(Set<TransactionModel> sentTransactions) {
        this.sentTransactions = sentTransactions;
    }

    public enum UserType {
        CUSTOMER,
        MERCHANT
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
