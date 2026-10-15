package com.mx.lv.ordermanagment.adapter.out.persistence.mongo.mapper;

import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document.CustomerDocument;
import com.mx.lv.ordermanagment.domain.model.Customer;

public class CustomerMongoMapper {

    public CustomerDocument mapCustomerDomainToDocument(Customer customer) {
        return new CustomerDocument(
                customer.userId(),
                customer.name(),
                customer.fathersLastName(),
                customer.mothersLastName(),
                customer.email(),
                customer.address()
        );
    }

    public Customer mapCustomerDocumentToDomain(CustomerDocument document) {
        return new Customer(
                document.customerId(),
                document.name(),
                document.fathersLastName(),
                document.mothersLastName(),
                document.email(),
                document.address()
        );
    }
}
