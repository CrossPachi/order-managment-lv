package com.mx.lv.ordermanagment.domain.model;

import java.util.List;

public record CustomWhitOrders(Customer customer,
                               List<Order> orders) {

    public CustomWhitOrders {
        orders = List.copyOf(orders);
    }
}
