package com.example.liskovbackend.global.handler;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 리소스를 찾을 수 없을 때 (ResourceNotFoundException)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    //리소스가 이미 존재할 때
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.CONFLICT);
    }
}
