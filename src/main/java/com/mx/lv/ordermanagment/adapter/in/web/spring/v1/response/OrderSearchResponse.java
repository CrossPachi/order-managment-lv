package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response;

import java.util.List;

public record OrderSearchResponse(String orderRef,
                                  String channel,
                                  String orderStatus,
                                  String storeName,
                                  List<OrderSearchItemResponse> items) {

    public OrderSearchResponse {
        items = List.copyOf(items);
    }
}
