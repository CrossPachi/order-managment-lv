package com.mx.lv.ordermanagment.adapter.out.persistence.mongo.mapper;

import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document.CustomerDocument;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMongoMapperTest {

    private final CustomerMongoMapper mapper = new CustomerMongoMapper();

    @Test
    void mapsCustomerToMongoDocument() {
        Customer customer = customer();

        CustomerDocument document = mapper.mapCustomerDomainToDocument(customer);

        assertEquals(new CustomerDocument(
                "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10"
        ), document);
    }

    @Test
    void mapsMongoDocumentBackToCustomer() {
        CustomerDocument document =
                new CustomerDocument("user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10");

        Customer customer = mapper.mapCustomerDocumentToDomain(document);

        assertEquals(customer(), customer);
    }

    private Customer customer() {
        return new Customer("user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10");
    }
}
