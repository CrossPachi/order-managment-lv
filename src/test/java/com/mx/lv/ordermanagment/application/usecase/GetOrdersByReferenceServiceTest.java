package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.OrderNotFoundException;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOrdersByReferenceServiceTest {

    @Mock
    private LoadOrdersPort loadOrdersPort;

    @Test
    void returnsTheOrderWithTheReferenceThePersonAskedFor() {
        Order firstOrder = order("ORD-1001");
        Order requestedOrder = order("ORD-2042");
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(firstOrder, requestedOrder));

        Order result = new GetOrdersByReferenceService(loadOrdersPort)
                .getOrdersByReference("ORD-2042");

        assertSame(requestedOrder, result);
        verify(loadOrdersPort).loadOrders();
    }

    @Test
    void reportsThatTheOrderWasNotFoundWhenThereAreNoOrders() {
        when(loadOrdersPort.loadOrders()).thenReturn(List.of());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> new GetOrdersByReferenceService(loadOrdersPort)
                        .getOrdersByReference("ORD-MISSING")
        );

        assertEquals("Order not found: ORD-MISSING", exception.getMessage());
    }

    @Test
    void reportsThatTheOrderWasNotFoundWhenItsReferenceDoesNotMatch() {
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(order("ORD-1001")));

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> new GetOrdersByReferenceService(loadOrdersPort)
                        .getOrdersByReference("ORD-9999")
        );

        assertEquals("Order not found: ORD-9999", exception.getMessage());
    }

    @Test
    void reportsNotFoundWhenTheReferenceIsBlankOrMissing() {
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(order("ORD-1001")));
        GetOrdersByReferenceService service = new GetOrdersByReferenceService(loadOrdersPort);

        assertThrows(OrderNotFoundException.class, () -> service.getOrdersByReference(""));
        assertThrows(OrderNotFoundException.class, () -> service.getOrdersByReference(null));
    }

    @Test
    void passesOnProblemsLoadingOrders() {
        OrdersUnavailableException expected = new OrdersUnavailableException("Orders are unavailable");
        when(loadOrdersPort.loadOrders()).thenThrow(expected);

        OrdersUnavailableException actual = assertThrows(
                OrdersUnavailableException.class,
                () -> new GetOrdersByReferenceService(loadOrdersPort)
                        .getOrdersByReference("ORD-2042")
        );

        assertSame(expected, actual);
    }

    private Order order(String reference) {
        return new Order(reference, "user-1", "WEB", "EN PROCESO", "Tienda Central", List.of());
    }
}
