package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.port.in.UpdateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;

public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final CustomerPersistencePort customerPersistencePort;

    public UpdateCustomerService(CustomerPersistencePort customerPersistencePort) {
        this.customerPersistencePort = customerPersistencePort;
    }

    @Override
    public Customer updateCustomer(Customer customer, String customerId) {
        validateCustomerExists(customerId);

        Customer customerToUpdate = new Customer(
                customerId,
                customer.name(),
                customer.fathersLastName(),
                customer.mothersLastName(),
                customer.email(),
                customer.address()
        );

        return customerPersistencePort.save(customerToUpdate);
    }

    private void validateCustomerExists(String customerId) {
        if (!customerPersistencePort.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
