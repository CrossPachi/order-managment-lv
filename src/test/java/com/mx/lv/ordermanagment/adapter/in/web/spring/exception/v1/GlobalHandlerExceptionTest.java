package com.mx.lv.ordermanagment.adapter.in.web.spring.exception.v1;

import com.mx.lv.ordermanagment.adapter.in.web.spring.response.v1.ApiErrorResponse;
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
}
