package com.mx.lv.ordermanagment.application.exception;

public class EmptyCustomerPatchException extends RuntimeException {

    public EmptyCustomerPatchException() {
        super("A field must be provided to update the customer");
    }
}
