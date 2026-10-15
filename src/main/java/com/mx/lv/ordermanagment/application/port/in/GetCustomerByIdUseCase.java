package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;

public interface GetCustomerByIdUseCase {

    CustomWhitOrders getCustomerById(String userId);
}
