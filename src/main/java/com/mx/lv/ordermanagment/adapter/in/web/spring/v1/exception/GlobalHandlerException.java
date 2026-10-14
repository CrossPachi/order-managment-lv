package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.exception;


import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.ApiErrorResponse;
import com.mx.lv.ordermanagment.application.exception.OrderNotFoundException;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(OrdersUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleOrdersUnavailable(OrdersUnavailableException exception) {
        return buildApiErrorResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException exception) {
        return buildApiErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> buildApiErrorResponse(HttpStatus status, String message) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}
