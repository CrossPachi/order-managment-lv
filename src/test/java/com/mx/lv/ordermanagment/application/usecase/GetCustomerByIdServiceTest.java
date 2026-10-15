package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomerByIdServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;
    @Mock
    private LoadOrdersPort loadOrdersPort;

    @Test
    void returnsCustomerWithOnlyTheirOrders() {
        Customer customer = customer("user-123");
        Order customersOrder = order("order-1", "user-123");
        Order someoneElsesOrder = order("order-2", "user-999");
        when(customerPersistencePort.findById("user-123")).thenReturn(Optional.of(customer));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(customersOrder, someoneElsesOrder));

        CustomWhitOrders result = new GetCustomerByIdService(customerPersistencePort, loadOrdersPort)
                .getCustomerById("user-123");

        assertEquals(new CustomWhitOrders(customer, List.of(customersOrder)), result);
    }

    @Test
    void returnsAnEmptyOrderListWhenTheCustomerHasNoOrders() {
        Customer customer = customer("user-123");
        when(customerPersistencePort.findById("user-123")).thenReturn(Optional.of(customer));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(order("order-2", "user-999")));

        CustomWhitOrders result = new GetCustomerByIdService(customerPersistencePort, loadOrdersPort)
                .getCustomerById("user-123");

        assertEquals(new CustomWhitOrders(customer, List.of()), result);
    }

    @Test
    void reportsNotFoundAndDoesNotLoadOrdersWhenTheCustomerDoesNotExist() {
        when(customerPersistencePort.findById("missing-user")).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> new GetCustomerByIdService(customerPersistencePort, loadOrdersPort)
                        .getCustomerById("missing-user")
        );

        assertEquals("Customer not found: missing-user", exception.getMessage());
        verify(loadOrdersPort, never()).loadOrders();
    }

    @Test
    void passesFailuresLoadingOrdersBackToTheCaller() {
        Customer customer = customer("user-123");
        OrdersUnavailableException expected = new OrdersUnavailableException("Orders are unavailable");
        when(customerPersistencePort.findById("user-123")).thenReturn(Optional.of(customer));
        when(loadOrdersPort.loadOrders()).thenThrow(expected);

        OrdersUnavailableException actual = assertThrows(
                OrdersUnavailableException.class,
                () -> new GetCustomerByIdService(customerPersistencePort, loadOrdersPort)
                        .getCustomerById("user-123")
        );

        assertSame(expected, actual);
    }

    private Customer customer(String id) {
        return new Customer(id, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }

    private Order order(String reference, String customerId) {
        return new Order(reference, customerId, "WEB", "PROCESSING", "Store", List.of());
    }
}
