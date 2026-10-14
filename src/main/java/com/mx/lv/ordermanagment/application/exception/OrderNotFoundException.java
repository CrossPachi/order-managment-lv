package com.mx.lv.ordermanagment.application.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super("Order not found: " + message);
    }
}
