package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;

import java.util.List;

public interface GetAllCustomersUseCase {
    List<CustomWhitOrders> getAllCustomers();
}
