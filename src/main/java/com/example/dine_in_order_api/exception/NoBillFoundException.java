package com.example.dine_in_order_api.exception;

public class NoBillFoundException extends RuntimeException{
    private String message;
    public NoBillFoundException(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
