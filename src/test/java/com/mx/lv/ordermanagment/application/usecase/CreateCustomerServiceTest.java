package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerAlreadyExistsException;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @Test
    void savesANewCustomerWithTheProvidedId() {
        Customer customer = customer("user-123");
        when(customerPersistencePort.existsById("user-123")).thenReturn(false);
        when(customerPersistencePort.save(customer)).thenReturn(customer);

        Customer created = new CreateCustomerService(customerPersistencePort).createCustomer(customer);

        assertEquals(customer, created);
        verify(customerPersistencePort).save(customer);
    }

    @Test
    void trimsTheProvidedIdBeforeCheckingAndSaving() {
        Customer input = customer("  user-123  ");
        Customer trimmedCustomer = customer("user-123");
        when(customerPersistencePort.existsById("user-123")).thenReturn(false);
        when(customerPersistencePort.save(trimmedCustomer)).thenReturn(trimmedCustomer);

        Customer created = new CreateCustomerService(customerPersistencePort).createCustomer(input);

        assertEquals(trimmedCustomer, created);
        verify(customerPersistencePort).existsById("user-123");
        verify(customerPersistencePort).save(trimmedCustomer);
    }

    @Test
    void createsAnIdWhenTheProvidedIdIsMissingOrBlank() {
        doReturn(false).when(customerPersistencePort).existsById(anyString());
        doAnswer(invocation -> invocation.getArgument(0))
                .when(customerPersistencePort).save(org.mockito.ArgumentMatchers.any(Customer.class));

        for (String missingId : new String[]{null, "", "   "}) {
            Customer input = customer(missingId);
            Customer created = new CreateCustomerService(customerPersistencePort).createCustomer(input);

            assertNotNull(created.userId());
            assertTrue(created.userId().matches(
                    "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
            ));
        }
    }

    @Test
    void doesNotSaveACustomerThatAlreadyExists() {
        Customer customer = customer("user-123");
        when(customerPersistencePort.existsById("user-123")).thenReturn(true);

        CustomerAlreadyExistsException exception = assertThrows(
                CustomerAlreadyExistsException.class,
                () -> new CreateCustomerService(customerPersistencePort).createCustomer(customer)
        );

        assertEquals("Customer: user-123 already exists.", exception.getMessage());
        verify(customerPersistencePort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void passesPersistenceFailuresBackToTheCaller() {
        Customer customer = customer("user-123");
        IllegalStateException expected = new IllegalStateException("Database is unavailable");
        when(customerPersistencePort.existsById("user-123")).thenThrow(expected);

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> new CreateCustomerService(customerPersistencePort).createCustomer(customer)
        );

        assertEquals(expected, actual);
        verify(customerPersistencePort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Customer customer(String id) {
        return new Customer(id, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }
}
