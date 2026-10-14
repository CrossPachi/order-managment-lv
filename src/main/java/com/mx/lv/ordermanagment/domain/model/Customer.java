package com.mx.lv.ordermanagment.domain.model;

public record Customer(String userId,
                       String name,
                       String fathersLastName,
                       String mothersLastName,
                       String email,
                       String address) {
}
