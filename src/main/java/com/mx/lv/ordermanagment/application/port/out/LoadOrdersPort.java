package com.mx.lv.ordermanagment.application.port.out;

import com.mx.lv.ordermanagment.domain.model.Order;

import java.util.List;

public interface LoadOrdersPort {

    List<Order> loadOrders();

}
