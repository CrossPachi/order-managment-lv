package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
}
