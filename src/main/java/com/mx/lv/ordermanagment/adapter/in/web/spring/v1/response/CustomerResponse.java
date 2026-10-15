package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response;

public record CustomerResponse(
        String userId,
        String name,
        String fathersLastName,
        String mothersLastName,
        String email,
        String address
) {
}
