package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response;

import java.util.List;

public record CustomerWithOrdersResponse(
        String userId,
        String name,
        String fathersLastName,
        String mothersLastName,
        String email,
        String address,
        List<String> orders
) {

    public CustomerWithOrdersResponse {
        orders = List.copyOf(orders);
    }
}
