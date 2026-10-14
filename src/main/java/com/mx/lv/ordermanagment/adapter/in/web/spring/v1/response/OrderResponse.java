package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response;

import java.util.List;

public record OrderResponse(
    String orderRef,
    String channel,
    String orderStatus,
    List<OrderItemResponse> items
) {

    public OrderResponse {
        items = List.copyOf(items);
    }
}
