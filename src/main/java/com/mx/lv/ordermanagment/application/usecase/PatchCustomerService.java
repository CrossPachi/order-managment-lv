package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.exception.EmptyCustomerPatchException;
import com.mx.lv.ordermanagment.application.model.PatchCustomerData;
import com.mx.lv.ordermanagment.application.port.in.PatchCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;

public class PatchCustomerService implements PatchCustomerUseCase {

    private final CustomerPersistencePort customerPersistencePort;

    public PatchCustomerService(CustomerPersistencePort customerPersistencePort) {
        this.customerPersistencePort = customerPersistencePort;
    }

    @Override
    public Customer patchCustomer(String customerId, PatchCustomerData patchData) {
        validatePatchHasChanges(patchData);
        Customer currentCustomer = customerPersistencePort
                .findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Customer patchedCustomer = new Customer(
                currentCustomer.userId(),
                resolveValue(patchData.name(), currentCustomer.name()),
                resolveValue(patchData.fathersLastName(), currentCustomer.fathersLastName()),
                resolveValue(patchData.mothersLastName(), currentCustomer.mothersLastName()),
                resolveValue(patchData.email(), currentCustomer.email()),
                resolveValue(patchData.address(), currentCustomer.address())
        );

        return customerPersistencePort.save(patchedCustomer);
    }

    private void validatePatchHasChanges(PatchCustomerData patchData) {
        if (!patchData.hasChanges()) {
            throw new EmptyCustomerPatchException();
        }
    }

    private String resolveValue(String newValue, String currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}
