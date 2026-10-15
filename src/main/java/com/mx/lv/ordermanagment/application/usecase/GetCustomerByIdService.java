package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.port.in.GetCustomerByIdUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import com.mx.lv.ordermanagment.domain.model.Order;

import java.util.List;

public class GetCustomerByIdService implements GetCustomerByIdUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final LoadOrdersPort loadOrdersPort;

    public GetCustomerByIdService(CustomerPersistencePort customerPersistencePort,
                                  LoadOrdersPort loadOrdersPort) {
        this.customerPersistencePort = customerPersistencePort;
        this.loadOrdersPort = loadOrdersPort;
    }

    @Override
    public CustomWhitOrders getCustomerById(String customerId) {
        Customer customer = customerPersistencePort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        List<Order> customerOrders = loadOrdersPort.loadOrders()
                .stream()
                .filter(order -> customer.userId().equals(order.userId()))
                .toList();
        return new CustomWhitOrders(customer, customerOrders);
    }

}
