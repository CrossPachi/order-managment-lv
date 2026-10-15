package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.controller;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.CustomerWebMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.CreateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.UpdateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerWithOrdersResponse;
import com.mx.lv.ordermanagment.application.port.in.CreateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetCustomerByIdUseCase;
import com.mx.lv.ordermanagment.application.port.in.UpdateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.DeleteCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetAllCustomersUseCase;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;
    private final GetAllCustomersUseCase getAllCustomersUseCase;

    private static final String CUSTOMER_ID_PATTERN = "^[a-zA-Z0-9-]{10,100}$";
    private static final String INVALID_CUSTOMER_ID_MESSAGE = "CustomerId invalid format";

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                              GetCustomerByIdUseCase getCustomerByIdUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              DeleteCustomerUseCase deleteCustomerUseCase,
                              GetAllCustomersUseCase getAllCustomersUseCase,
                              CustomerWebMapper customerWebMapper) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
        this.getAllCustomersUseCase = getAllCustomersUseCase;
        this.customerWebMapper = customerWebMapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerWebMapper.mapCreateRequestToDomain(request);
        Customer createdCustomer = createCustomerUseCase.createCustomer(customer);
        CustomerResponse response = customerWebMapper.mapDomainToResponse(createdCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerWithOrdersResponse> getCustomerById(
            @PathVariable @Pattern(regexp = CUSTOMER_ID_PATTERN,
            message = INVALID_CUSTOMER_ID_MESSAGE) String customerId) {
        CustomWhitOrders customerWithOrders = getCustomerByIdUseCase.getCustomerById(customerId);
        CustomerWithOrdersResponse response = customerWebMapper.mapDomainToResponse(customerWithOrders);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable @Pattern(regexp = CUSTOMER_ID_PATTERN, message = INVALID_CUSTOMER_ID_MESSAGE) String customerId,
                                                           @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = customerWebMapper.mapUpdateRequestToDomain(customerId, request);
        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customer, customerId);
        CustomerResponse response = customerWebMapper.mapDomainToResponse(updatedCustomer);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @Pattern(regexp = CUSTOMER_ID_PATTERN,
            message = INVALID_CUSTOMER_ID_MESSAGE) String customerId) {
        deleteCustomerUseCase.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerWithOrdersResponse>> getAllCustomers() {
        List<CustomWhitOrders> customersWithOrders = getAllCustomersUseCase.getAllCustomers();
        List<CustomerWithOrdersResponse> response = customerWebMapper.mapDomainListToResponse(customersWithOrders);
        return ResponseEntity.ok(response);
    }
}
