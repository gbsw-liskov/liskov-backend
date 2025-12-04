package com.example.liskovbackend.global.exception;

public class AiNoResponseException extends RuntimeException {
    public AiNoResponseException(String message) {
        super(message);
    }

}
