package com.example.dine_in_order_api.exception.Handler;

import com.example.dine_in_order_api.exception.CustomAuthenticationException;
import com.example.dine_in_order_api.exception.FoodNotFoundException;
import com.example.dine_in_order_api.utility.ResponseBuilder;
import com.example.dine_in_order_api.utility.SimpleErrorStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BadCredentialsExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<SimpleErrorStructure> handleBadCredentialsException(BadCredentialsException e){
        return ResponseBuilder.notFound(e.getMessage());
    }
}
