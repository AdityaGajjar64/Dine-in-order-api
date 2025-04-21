package com.example.dine_in_order_api.exception;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException {
    private String message;
    public InvalidPasswordException(String s) {
        message=s;
    }
}
