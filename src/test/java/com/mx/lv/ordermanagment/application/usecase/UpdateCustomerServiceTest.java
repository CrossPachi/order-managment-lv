package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @Test
    void updatesAnExistingCustomerUsingTheIdFromThePath() {
        Customer requestCustomer = customer("different-id", "Updated");
        Customer expectedSavedCustomer = customer("customer-123", "Updated");
        when(customerPersistencePort.existsById("customer-123")).thenReturn(true);
        when(customerPersistencePort.save(expectedSavedCustomer)).thenReturn(expectedSavedCustomer);

        Customer result = new UpdateCustomerService(customerPersistencePort)
                .updateCustomer(requestCustomer, "customer-123");

        assertEquals(expectedSavedCustomer, result);
        verify(customerPersistencePort).save(expectedSavedCustomer);
    }

    @Test
    void reportsNotFoundAndDoesNotSaveWhenTheCustomerDoesNotExist() {
        when(customerPersistencePort.existsById("missing-user")).thenReturn(false);

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> new UpdateCustomerService(customerPersistencePort)
                        .updateCustomer(customer("missing-user", "Alex"), "missing-user")
        );

        assertEquals("Customer not found: missing-user", exception.getMessage());
        verify(customerPersistencePort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void passesPersistenceFailuresBackToTheCaller() {
        Customer customer = customer("customer-123", "Alex");
        IllegalStateException expected = new IllegalStateException("Database is unavailable");
        when(customerPersistencePort.existsById("customer-123")).thenReturn(true);
        when(customerPersistencePort.save(customer)).thenThrow(expected);

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> new UpdateCustomerService(customerPersistencePort)
                        .updateCustomer(customer, "customer-123")
        );

        assertSame(expected, actual);
    }

    private Customer customer(String id, String name) {
        return new Customer(id, name, "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }
}
