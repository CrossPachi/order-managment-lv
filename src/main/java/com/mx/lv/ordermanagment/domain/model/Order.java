package com.mx.lv.ordermanagment.domain.model;

import java.util.List;

public record Order(
        String orderRef,
        String userId,
        String channel,
        String orderStatus,
        String storeName,
        List<Item> items
) {

    public Order {
        items = List.copyOf(items);
    }
}
