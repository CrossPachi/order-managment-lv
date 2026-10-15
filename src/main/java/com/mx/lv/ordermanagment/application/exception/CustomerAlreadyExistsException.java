package com.mx.lv.ordermanagment.application.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message) {
        super("Customer: " + message + " already exists.");
    }
}
