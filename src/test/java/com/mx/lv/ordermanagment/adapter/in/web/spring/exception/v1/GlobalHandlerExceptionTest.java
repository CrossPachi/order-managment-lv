package com.mx.lv.ordermanagment.adapter.in.web.spring.exception.v1;


import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.exception.GlobalHandlerException;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.ApiErrorResponse;
import com.mx.lv.ordermanagment.application.exception.CustomerAlreadyExistsException;
import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.exception.OrderNotFoundException;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalHandlerExceptionTest {

    @Test
    void convertsOrdersUnavailableToBadGatewayWithAnErrorBody() {
        GlobalHandlerException handler = new GlobalHandlerException();

        ResponseEntity<ApiErrorResponse> response =
                handler.handleOrdersUnavailable(new OrdersUnavailableException("orders are unavailable"));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_GATEWAY.value(), response.getBody().status());
        assertEquals(HttpStatus.BAD_GATEWAY.getReasonPhrase(), response.getBody().error());
        assertEquals("orders are unavailable", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void tellsTheCallerWhenAnOrderCouldNotBeFound() {
        GlobalHandlerException handler = new GlobalHandlerException();

        ResponseEntity<ApiErrorResponse> response =
                handler.handleOrderNotFound(new OrderNotFoundException("ORD-MISSING"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response.getBody().error());
        assertEquals("Order not found: ORD-MISSING", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void reportsAConflictWhenTheCustomerAlreadyExists() {
        GlobalHandlerException handler = new GlobalHandlerException();

        ResponseEntity<ApiErrorResponse> response =
                handler.handleCustomerAlreadyExists(new CustomerAlreadyExistsException("customer-123"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().status());
        assertEquals("Customer: customer-123 already exists.", response.getBody().message());
    }

    @Test
    void reportsNotFoundWhenTheCustomerDoesNotExist() {
        GlobalHandlerException handler = new GlobalHandlerException();

        ResponseEntity<ApiErrorResponse> response =
                handler.handleCustomerNotFound(new CustomerNotFoundException("customer-123"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
        assertEquals("Customer not found: customer-123", response.getBody().message());
    }
}
