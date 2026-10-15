package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.CreateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.UpdateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerWithOrdersResponse;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import com.mx.lv.ordermanagment.domain.model.Order;

import java.util.List;

public class CustomerWebMapper {

    public Customer mapCreateRequestToDomain(CreateCustomerRequest request) {
        return new Customer(
                request.userId(),
                request.name(),
                request.fathersLastName(),
                request.mothersLastName(),
                request.email(),
                request.address()
        );
    }

    public Customer mapUpdateRequestToDomain(String customerId, UpdateCustomerRequest request) {
        return new Customer(
                customerId,
                request.name(),
                request.fathersLastName(),
                request.mothersLastName(),
                request.email(),
                request.address()
        );
    }

    public CustomerResponse mapDomainToResponse(Customer customer) {
        return new CustomerResponse(
                customer.userId(),
                customer.name(),
                customer.fathersLastName(),
                customer.mothersLastName(),
                customer.email(),
                customer.address()
        );
    }

    public CustomerWithOrdersResponse mapDomainToResponse(CustomWhitOrders customerWithOrders) {
        Customer customer = customerWithOrders.customer();
        List<String> orderReferences = customerWithOrders.orders()
                .stream()
                .map(Order::orderRef)
                .toList();

        return new CustomerWithOrdersResponse(
                customer.userId(),
                customer.name(),
                customer.fathersLastName(),
                customer.mothersLastName(),
                customer.email(),
                customer.address(),
                orderReferences
        );
    }

    public List<CustomerWithOrdersResponse> mapDomainListToResponse(List<CustomWhitOrders> customersWithOrders) {
        return customersWithOrders.stream()
                .map(customerWithOrders -> mapDomainToResponse(customerWithOrders))
                .toList();
    }
}
