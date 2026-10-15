package com.mx.lv.ordermanagment.application.port.out;

import com.mx.lv.ordermanagment.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerPersistencePort {

    Customer save(Customer customer);

    Optional<Customer> findById(String userId);

    boolean existsById(String userId);

    void deleteById(String userId);

    List<Customer> findAll();

    long count();
}
