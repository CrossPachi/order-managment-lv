package com.mx.lv.ordermanagment.adapter.out.persistence.mongo;

import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document.CustomerDocument;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.mapper.CustomerMongoMapper;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.repository.CustomerMongoRepository;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public class CustomerMongoAdapter implements CustomerPersistencePort {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerMongoMapper customerMongoMapper;

    public CustomerMongoAdapter(CustomerMongoRepository customerMongoRepository,
                                CustomerMongoMapper customerMongoMapper) {

        this.customerMongoRepository = customerMongoRepository;
        this.customerMongoMapper = customerMongoMapper;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerDocument document = customerMongoMapper.mapCustomerDomainToDocument(customer);
        CustomerDocument savedDocument = customerMongoRepository.save(document);
        return customerMongoMapper.mapCustomerDocumentToDomain(savedDocument);
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        Optional<CustomerDocument> customerDocument = customerMongoRepository.findById(customerId);
        return customerDocument.map(document ->
                customerMongoMapper.mapCustomerDocumentToDomain(document)
        );
    }


    @Override
    public boolean existsById(String customerId) {
        return customerMongoRepository.existsById(customerId);
    }

    @Override
    public void deleteById(String customerId) {
        customerMongoRepository.deleteById(customerId);
    }

    @Override
    public long count() {
        return customerMongoRepository.count();
    }

    @Override
    public List<Customer> findAll() {
        return customerMongoRepository.findAll()
                .stream()
                .map(document -> customerMongoMapper.mapCustomerDocumentToDomain(document))
                .toList();
    }
}
