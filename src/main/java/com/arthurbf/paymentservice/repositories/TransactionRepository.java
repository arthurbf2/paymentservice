package com.arthurbf.paymentservice.repositories;

import com.arthurbf.paymentservice.models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, UUID> {
}
