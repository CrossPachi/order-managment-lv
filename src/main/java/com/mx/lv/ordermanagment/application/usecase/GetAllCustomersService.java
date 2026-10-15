package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.port.in.GetAllCustomersUseCase;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import com.mx.lv.ordermanagment.domain.model.Order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetAllCustomersService implements GetAllCustomersUseCase {

    private final CustomerPersistencePort customerPersistencePort;
    private final LoadOrdersPort loadOrdersPort;

    public GetAllCustomersService(CustomerPersistencePort customerPersistencePort, LoadOrdersPort loadOrdersPort) {
        this.customerPersistencePort = customerPersistencePort;
        this.loadOrdersPort = loadOrdersPort;
    }

    @Override
    public List<CustomWhitOrders> getAllCustomers() {
        List<Customer> customers = customerPersistencePort.findAll();
        List<Order> orders = loadOrdersPort.loadOrders();
        Map<String, List<Order>> ordersByCustomerId = groupOrdersByCustomerId(orders);

        return customers.stream()
                .map(customer -> buildCustomerWithOrders(customer, ordersByCustomerId))
                .toList();
    }

    private Map<String, List<Order>> groupOrdersByCustomerId(
            List<Order> orders
    ) {
        return orders.stream()
                .filter(order -> order.userId() != null)
                .collect(Collectors.groupingBy(Order::userId));
    }

    private CustomWhitOrders buildCustomerWithOrders(Customer customer, Map<String, List<Order>> ordersByCustomerId) {
        List<Order> customerOrders = ordersByCustomerId.getOrDefault(customer.userId(), List.of());
        return new CustomWhitOrders(customer, customerOrders);
    }
}
