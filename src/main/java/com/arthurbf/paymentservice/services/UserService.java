package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.UserRecordDto;
import com.arthurbf.paymentservice.exceptions.UserAlreadyExistsException;
import com.arthurbf.paymentservice.exceptions.UserNotFoundException;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void saveUser(UserModel user) {
        userRepository.save(user);
    }

    @Transactional
    public UserModel createUser(UserRecordDto userRecordDto) {
        var userDb = userRepository.findByCpfcnpjOrEmail(userRecordDto.cpfcnpj(), userRecordDto.email());
        if (userDb.isPresent()) {
            throw new UserAlreadyExistsException();
        }
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

    public void validateUser(UUID id) {
        Optional<UserModel> user = getUser(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
    }

    public Optional<UserModel> getUser(UUID id) {
        return userRepository.findById(id);
    }

    public List<UserModel> getUsers() {
        return userRepository.findAll();
    }
}
