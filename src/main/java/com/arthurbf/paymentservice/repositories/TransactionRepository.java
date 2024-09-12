package com.arthurbf.paymentservice.repositories;

import com.arthurbf.paymentservice.models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, UUID> {
    @Query("SELECT t FROM TransactionModel t WHERE t.sender.id = :id OR t.receiver.id = :id")
    List<TransactionModel> findAllByUserId(@Param("id") UUID id);
}
