package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.Customer;

public interface CreateCustomerUseCase {

    Customer createCustomer(Customer customer);
}
