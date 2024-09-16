package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AuthorizationService {
    @Autowired
    private RestTemplate restTemplate;

    public boolean authorizeTransaction(UserModel sender, BigDecimal amount) {
        ResponseEntity<Map> authResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
        if (authResponse.getStatusCode() == HttpStatus.OK) {
            String message = (String) authResponse.getBody().get("status");
            return "success".equals(message);
        }
        return false;
    }
}
