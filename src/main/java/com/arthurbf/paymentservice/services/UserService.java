package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.UserRecordDto;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public UserModel saveUser(UserRecordDto userRecordDto) {
        var userDb = userRepository.findByCpfcnpjOrEmail(userRecordDto.cpfcnpj(), userRecordDto.email());
        if (userDb.isPresent())
            throw new RuntimeException("User already exists with the provided cpfcnpj or email.");
        UserModel user = new UserModel();
        user.setName(userRecordDto.name());
        user.setEmail(userRecordDto.email());
        user.setBalance(userRecordDto.balance());
        user.setPassword(userRecordDto.password());
        user.setCpfcnpj(userRecordDto.cpfcnpj());
        user.setUserType(userRecordDto.userType());
        user.setReceivedTransactions(new HashSet<>());
        user.setSentTransactions(new HashSet<>());
        return userRepository.save(user);
    }
}
