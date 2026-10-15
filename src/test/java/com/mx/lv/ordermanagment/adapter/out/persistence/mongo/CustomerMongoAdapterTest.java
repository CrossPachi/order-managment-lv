package com.mx.lv.ordermanagment.adapter.out.persistence.mongo;

import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document.CustomerDocument;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.mapper.CustomerMongoMapper;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.repository.CustomerMongoRepository;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerMongoAdapterTest {

    @Mock
    private CustomerMongoRepository repository;

    private final CustomerMongoMapper mapper = new CustomerMongoMapper();

    @Test
    void savesCustomerAndReturnsTheSavedRecordAsADomainCustomer() {
        Customer customer = customer();
        CustomerDocument savedDocument = document();
        when(repository.save(mapper.mapCustomerDomainToDocument(customer))).thenReturn(savedDocument);

        Customer saved = adapter().save(customer);

        assertEquals(customer, saved);
        verify(repository).save(mapper.mapCustomerDomainToDocument(customer));
    }

    @Test
    void returnsACustomerWhenTheIdIsFound() {
        when(repository.findById("user-123")).thenReturn(Optional.of(document()));

        Optional<Customer> result = adapter().findById("user-123");

        assertEquals(Optional.of(customer()), result);
    }

    @Test
    void returnsAnEmptyResultWhenTheIdIsNotFound() {
        when(repository.findById("missing-user")).thenReturn(Optional.empty());

        assertTrue(adapter().findById("missing-user").isEmpty());
    }

    @Test
    void returnsAllCustomersFromTheRepository() {
        when(repository.findAll()).thenReturn(java.util.List.of(document()));

        assertEquals(java.util.List.of(customer()), adapter().findAll());
    }

    @Test
    void returnsAnEmptyListWhenTheRepositoryHasNoCustomers() {
        when(repository.findAll()).thenReturn(java.util.List.of());

        assertTrue(adapter().findAll().isEmpty());
    }

    @Test
    void reportsWhetherTheIdExists() {
        when(repository.existsById("user-123")).thenReturn(true);
        when(repository.existsById("missing-user")).thenReturn(false);

        assertTrue(adapter().existsById("user-123"));
        assertFalse(adapter().existsById("missing-user"));
    }

    @Test
    void deletesById() {
        adapter().deleteById("user-123");

        verify(repository).deleteById("user-123");
    }

    @Test
    void passesRepositoryFailuresBackToTheCaller() {
        IllegalStateException expected = new IllegalStateException("Mongo is unavailable");
        doThrow(expected).when(repository).deleteById("user-123");

        IllegalStateException actual = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> adapter().deleteById("user-123")
        );

        assertSame(expected, actual);
    }

    private CustomerMongoAdapter adapter() {
        return new CustomerMongoAdapter(repository, mapper);
    }

    private Customer customer() {
        return new Customer("user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10");
    }

    private CustomerDocument document() {
        return new CustomerDocument(
                "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10"
        );
    }
}
