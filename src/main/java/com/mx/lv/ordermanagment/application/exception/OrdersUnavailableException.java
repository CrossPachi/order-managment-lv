package com.mx.lv.ordermanagment.application.exception;

public class OrdersUnavailableException extends RuntimeException {

    public OrdersUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrdersUnavailableException(String message) {
        super(message);
    }
}
