package com.mx.lv.ordermanagment.adapter.out.http.dto;

import java.util.List;

public record MockApiOrderResponse(
        String orderRef,
        String userId,
        String canal,
        String orderStatus,
        boolean marketPlace,
        boolean giftRegistry,
        List<String> items,
        String storeName,
        String id
) {

    public MockApiOrderResponse {
        items = List.copyOf(items);
    }
}
