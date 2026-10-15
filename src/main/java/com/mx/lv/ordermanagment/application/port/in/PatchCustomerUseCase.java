package com.mx.lv.ordermanagment.application.port.in;

import com.mx.lv.ordermanagment.application.model.PatchCustomerData;
import com.mx.lv.ordermanagment.domain.model.Customer;

public interface PatchCustomerUseCase {

    Customer patchCustomer(String customerId, PatchCustomerData patchData);

}
