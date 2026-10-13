package com.mx.lv.ordermanagment.adapter.in.web.spring.exception.v1;

import com.mx.lv.ordermanagment.adapter.in.web.spring.response.v1.ApiErrorResponse;
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
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}
