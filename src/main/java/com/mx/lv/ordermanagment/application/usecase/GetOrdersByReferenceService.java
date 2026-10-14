package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.OrderNotFoundException;
import com.mx.lv.ordermanagment.application.port.in.GetOrderByReferenceUseCase;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.Order;

public class GetOrdersByReferenceService implements GetOrderByReferenceUseCase {

    private final LoadOrdersPort loadOrdersPort;

    public GetOrdersByReferenceService(LoadOrdersPort loadOrdersPort) {
        this.loadOrdersPort = loadOrdersPort;
    }

    @Override
    public Order getOrdersByReference(String reference) {
        return loadOrdersPort.loadOrders().stream().filter(order -> order.orderRef().equals(reference))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException(reference));
    }
}
