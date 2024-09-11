package com.arthurbf.paymentservice.repositories;

import com.arthurbf.paymentservice.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    @Query("SELECT u FROM UserModel u WHERE u.cpfcnpj = :cpfcnpj OR u.email = :email")
    Optional<UserModel> findByCpfcnpjOrEmail(String cpfcnpj, String email);
}
