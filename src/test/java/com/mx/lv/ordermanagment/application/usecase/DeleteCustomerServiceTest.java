package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCustomerServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @Test
    void deletesTheCustomerWhenTheyExist() {
        when(customerPersistencePort.existsById("customer-123")).thenReturn(true);

        new DeleteCustomerService(customerPersistencePort).deleteCustomer("customer-123");

        verify(customerPersistencePort).deleteById("customer-123");
    }

    @Test
    void reportsNotFoundAndDoesNotDeleteWhenTheCustomerIsMissing() {
        when(customerPersistencePort.existsById("missing-user")).thenReturn(false);

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> new DeleteCustomerService(customerPersistencePort).deleteCustomer("missing-user")
        );

        assertEquals("Customer not found: missing-user", exception.getMessage());
        verify(customerPersistencePort, never()).deleteById("missing-user");
    }

    @Test
    void passesPersistenceFailuresBackToTheCaller() {
        IllegalStateException expected = new IllegalStateException("Database is unavailable");
        when(customerPersistencePort.existsById("customer-123")).thenReturn(true);
        org.mockito.Mockito.doThrow(expected).when(customerPersistencePort).deleteById("customer-123");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> new DeleteCustomerService(customerPersistencePort).deleteCustomer("customer-123")
        );

        assertEquals(expected, actual);
    }
}
