package com.mx.lv.ordermanagment.adapter.in.web.spring.response.v1;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
}
