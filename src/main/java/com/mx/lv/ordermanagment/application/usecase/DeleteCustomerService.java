package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.port.in.DeleteCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;

public class DeleteCustomerService implements DeleteCustomerUseCase {

    private final CustomerPersistencePort customerPersistencePort;

    public DeleteCustomerService(CustomerPersistencePort customerPersistencePort) {
        this.customerPersistencePort = customerPersistencePort;
    }

    @Override
    public void deleteCustomer(String customerId) {
        validateCustomerExists(customerId);
        customerPersistencePort.deleteById(customerId);
    }

    private void validateCustomerExists(String customerId) {
        if (!customerPersistencePort.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
