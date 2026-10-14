package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.Order;

public interface GetOrderByReferenceUseCase {

    Order getOrdersByReference(String reference);
}
