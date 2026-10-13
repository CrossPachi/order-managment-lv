package com.mx.lv.ordermanagment.domain.model;

public record Item(
        String itemId,
        String skuId,
        int quantity,
        String displayName,
        String deliveryStatus
) {
}
