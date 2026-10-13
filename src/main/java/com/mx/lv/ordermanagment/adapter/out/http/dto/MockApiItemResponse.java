package com.mx.lv.ordermanagment.adapter.out.http.dto;

public record MockApiItemResponse(
        String itemId,
        String skuId,
        int quantity,
        String displayName,
        String deliveryStatus,
        String id
) {
}
