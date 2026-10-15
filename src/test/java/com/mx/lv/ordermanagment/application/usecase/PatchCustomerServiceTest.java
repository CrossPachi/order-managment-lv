package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.exception.EmptyCustomerPatchException;
import com.mx.lv.ordermanagment.application.model.PatchCustomerData;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatchCustomerServiceTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @Test
    void updatesOnlyTheFieldsProvidedInThePatch() {
        Customer current = customer("Ana", "Lopez", "Diaz", "ana@example.com", "Old address");
        Customer expected = customer("Ana María", "Lopez", "Diaz", "ana@example.com", "Old address");
        PatchCustomerData patch = new PatchCustomerData("Ana María", null, null, null, null);
        when(customerPersistencePort.findById("customer-123")).thenReturn(Optional.of(current));
        when(customerPersistencePort.save(expected)).thenReturn(expected);

        Customer result = new PatchCustomerService(customerPersistencePort)
                .patchCustomer("customer-123", patch);

        assertEquals(expected, result);
        verify(customerPersistencePort).save(expected);
    }

    @Test
    void canUpdateSeveralFieldsAtOnce() {
        Customer current = customer("Ana", "Lopez", "Diaz", "ana@example.com", "Old address");
        Customer expected = customer("Ana", "Lopez", "Diaz", "new@example.com", "New address");
        PatchCustomerData patch = new PatchCustomerData(null, null, null, "new@example.com", "New address");
        when(customerPersistencePort.findById("customer-123")).thenReturn(Optional.of(current));
        when(customerPersistencePort.save(expected)).thenReturn(expected);

        assertEquals(expected, new PatchCustomerService(customerPersistencePort)
                .patchCustomer("customer-123", patch));
    }

    @Test
    void rejectsAnEmptyPatchWithoutLookingUpOrSavingTheCustomer() {
        EmptyCustomerPatchException exception = assertThrows(
                EmptyCustomerPatchException.class,
                () -> new PatchCustomerService(customerPersistencePort)
                        .patchCustomer("customer-123", new PatchCustomerData(null, null, null, null, null))
        );

        assertEquals("A field must be provided to update the customer", exception.getMessage());
        verify(customerPersistencePort, never()).findById("customer-123");
        verify(customerPersistencePort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reportsNotFoundWhenThereIsNoCustomerToUpdate() {
        when(customerPersistencePort.findById("missing-user")).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> new PatchCustomerService(customerPersistencePort)
                        .patchCustomer("missing-user", new PatchCustomerData("Ana", null, null, null, null))
        );

        assertEquals("Customer not found: missing-user", exception.getMessage());
        verify(customerPersistencePort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void passesPersistenceFailuresBackToTheCaller() {
        IllegalStateException expected = new IllegalStateException("Database is unavailable");
        when(customerPersistencePort.findById("customer-123")).thenThrow(expected);

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> new PatchCustomerService(customerPersistencePort)
                        .patchCustomer("customer-123", new PatchCustomerData("Ana", null, null, null, null))
        );

        assertSame(expected, actual);
    }

    private Customer customer(String name, String fathersLastName, String mothersLastName,
                              String email, String address) {
        return new Customer("customer-123", name, fathersLastName, mothersLastName, email, address);
    }
}
