package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.UserRecordDto;
import com.arthurbf.paymentservice.exceptions.UserAlreadyExistsException;
import com.arthurbf.paymentservice.models.UserModel;
import com.arthurbf.paymentservice.repositories.UserRepository;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private Validator validator;

    @Autowired
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testCreateUser_success(){
        when(userRepository.findByCpfcnpjOrEmail(any(), any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new UserModel());
        var userDto = new UserRecordDto("John Doe", "john@gmail.com", new BigDecimal(100), "12345678", "500.256.890-45",
                UserModel.UserType.CUSTOMER, new HashSet<>(), new HashSet<>());

        userService.createUser(userDto);
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void testCreateUser_fail1() throws UserAlreadyExistsException {
        when(userRepository.findByCpfcnpjOrEmail(any(), any())).thenReturn(Optional.of(new UserModel()));
        UserAlreadyExistsException e = assertThrows(UserAlreadyExistsException.class, () -> {
            var userDto = new UserRecordDto("John Doe", "john@gmail.com", new BigDecimal(100), "12345678", "500.256.890-45",
                    UserModel.UserType.CUSTOMER, new HashSet<>(), new HashSet<>());
            userService.createUser(userDto);
        });
        assertEquals(e.getMessage(), "There already exists a user with this CPF/email");
    }

    @Test
    @DisplayName("Should fail if email is invalid")
    void testCreateUser_fail2() {
        when(userRepository.findByCpfcnpjOrEmail(any(), any())).thenReturn(Optional.empty());
        var userDto = new UserRecordDto("John Doe", "bademail", new BigDecimal(5), "1234", "500.256.890-45",
                UserModel.UserType.CUSTOMER, new HashSet<>(), new HashSet<>());
        UserModel user = userService.createUser(userDto);
        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail if CPF is invalid")
    void testCreateUser_fail3() {
        when(userRepository.findByCpfcnpjOrEmail(any(), any())).thenReturn(Optional.empty());
        var userDto = new UserRecordDto("John Doe", "john@gmail.com", new BigDecimal(5), "1234", "123.456.789-02",
                UserModel.UserType.CUSTOMER, new HashSet<>(), new HashSet<>());
        UserModel user = userService.createUser(userDto);
        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
