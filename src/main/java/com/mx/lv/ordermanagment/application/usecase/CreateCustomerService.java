package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerAlreadyExistsException;
import com.mx.lv.ordermanagment.application.port.in.CreateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;

import java.util.UUID;

public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerPersistencePort customerPersistencePort;

    public CreateCustomerService(CustomerPersistencePort customerPersistencePort) {
        this.customerPersistencePort = customerPersistencePort;
    }


    @Override
    public Customer createCustomer(Customer customer) {
        String customerId = checkUserId(customer.userId());
        validateCustomerDoesNotExist(customerId);
        Customer customerToSave = new Customer(customerId,
                customer.name(),
                customer.fathersLastName(),
                customer.mothersLastName(),
                customer.email(), customer.address());
        return customerPersistencePort.save(customerToSave);
    }

    private String checkUserId(String customerId){
        if(customerId == null || customerId.isBlank()){
            return UUID.randomUUID().toString();
        }

        return customerId.trim();
    }

    private void validateCustomerDoesNotExist(String customerId) {
        if (customerPersistencePort.existsById(customerId)) {
            throw new CustomerAlreadyExistsException(customerId);
        }
    }
}
