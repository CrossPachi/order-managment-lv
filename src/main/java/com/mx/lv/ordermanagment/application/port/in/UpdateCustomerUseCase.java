package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.Customer;

public interface UpdateCustomerUseCase {

    Customer updateCustomer(Customer customer, String customerId);
}
