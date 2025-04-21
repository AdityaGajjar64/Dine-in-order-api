package com.example.dine_in_order_api.service;

import com.example.dine_in_order_api.dto.request.AuthRecord;
import com.example.dine_in_order_api.dto.request.LoginRequest;
import com.example.dine_in_order_api.dto.request.ResetPasswordRequest;
import org.springframework.http.HttpHeaders;

public interface AuthService {
    
    AuthRecord login(LoginRequest loginRequest);

    AuthRecord refreshLogin(String refreshToken);

    HttpHeaders logout(String refreshToken, String accessToken);

}
