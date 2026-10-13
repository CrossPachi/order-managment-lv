package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.Order;

import java.util.List;

public interface SearchOrdersUseCase {

    List<Order> searchOrders(String text);
}
