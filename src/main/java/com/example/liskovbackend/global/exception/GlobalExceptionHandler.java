package com.example.liskovbackend.global.exception;

import com.example.liskovbackend.common.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e){
//        return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ResourceAlreadyExistsException.class)
//    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e){
//        return ApiResponse.error(e.getMessage(), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(AiNoResponseException.class)
//    public ResponseEntity<ApiResponse<Void>> handleAiNoResponseException(AiNoResponseException e){
//        return ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(ExternalServiceException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AiNoResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiNoResponseException(AiNoResponseException e){
        return ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
