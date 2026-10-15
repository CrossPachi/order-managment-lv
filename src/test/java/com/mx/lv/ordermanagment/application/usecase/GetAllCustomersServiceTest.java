package com.mx.lv.ordermanagment.application.usecase;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllCustomersServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;
    @Mock
    private LoadOrdersPort loadOrdersPort;

    @Test
    void returnsEachCustomerWithOnlyTheirOrders() {
        Customer firstCustomer = customer("user-1");
        Customer secondCustomer = customer("user-2");
        Order firstOrder = order("order-1", "user-1");
        Order secondOrder = order("order-2", "user-2");
        Order unlinkedOrder = order("order-3", "unknown-user");
        when(customerPersistencePort.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(firstOrder, secondOrder, unlinkedOrder));

        List<CustomWhitOrders> result =
                new GetAllCustomersService(customerPersistencePort, loadOrdersPort).getAllCustomers();

        assertEquals(List.of(
                new CustomWhitOrders(firstCustomer, List.of(firstOrder)),
                new CustomWhitOrders(secondCustomer, List.of(secondOrder))
        ), result);
    }

    @Test
    void returnsAnEmptyListWhenThereAreNoCustomers() {
        when(customerPersistencePort.findAll()).thenReturn(List.of());
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(order("order-1", "user-1")));

        assertEquals(List.of(),
                new GetAllCustomersService(customerPersistencePort, loadOrdersPort).getAllCustomers());
    }

    @Test
    void keepsCustomersWithoutOrdersAndSkipsOrdersWithoutCustomerIds() {
        Customer customer = customer("user-1");
        when(customerPersistencePort.findAll()).thenReturn(List.of(customer));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(order("order-1", null)));

        List<CustomWhitOrders> result =
                new GetAllCustomersService(customerPersistencePort, loadOrdersPort).getAllCustomers();

        assertEquals(List.of(new CustomWhitOrders(customer, List.of())), result);
    }

    @Test
    void passesOrderServiceFailuresBackToTheCaller() {
        OrdersUnavailableException expected = new OrdersUnavailableException("Orders are unavailable");
        when(customerPersistencePort.findAll()).thenReturn(List.of(customer("user-1")));
        when(loadOrdersPort.loadOrders()).thenThrow(expected);

        OrdersUnavailableException actual = assertThrows(
                OrdersUnavailableException.class,
                () -> new GetAllCustomersService(customerPersistencePort, loadOrdersPort).getAllCustomers()
        );

        assertSame(expected, actual);
    }

    private Customer customer(String id) {
        return new Customer(id, "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10");
    }

    private Order order(String reference, String userId) {
        return new Order(reference, userId, "WEB", "PROCESSING", "Store", List.of());
    }
}
