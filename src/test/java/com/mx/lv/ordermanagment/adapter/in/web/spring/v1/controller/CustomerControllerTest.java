package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.controller;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.CustomerWebMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.CreateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.PatchCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.UpdateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerWithOrdersResponse;
import com.mx.lv.ordermanagment.application.exception.CustomerAlreadyExistsException;
import com.mx.lv.ordermanagment.application.exception.CustomerNotFoundException;
import com.mx.lv.ordermanagment.application.exception.EmptyCustomerPatchException;
import com.mx.lv.ordermanagment.application.model.PatchCustomerData;
import com.mx.lv.ordermanagment.application.port.in.CreateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.DeleteCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetAllCustomersUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetCustomerByIdUseCase;
import com.mx.lv.ordermanagment.application.port.in.PatchCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.UpdateCustomerUseCase;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private static final String CUSTOMER_ID = "customer-123";

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;
    @Mock
    private GetCustomerByIdUseCase getCustomerByIdUseCase;
    @Mock
    private GetAllCustomersUseCase getAllCustomersUseCase;
    @Mock
    private PatchCustomerUseCase patchCustomerUseCase;
    @Mock
    private UpdateCustomerUseCase updateCustomerUseCase;
    @Mock
    private DeleteCustomerUseCase deleteCustomerUseCase;
    @Mock
    private CustomerWebMapper customerWebMapper;

    private CustomerController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomerController(
                createCustomerUseCase,
                getCustomerByIdUseCase,
                updateCustomerUseCase,
                deleteCustomerUseCase,
                getAllCustomersUseCase,
                patchCustomerUseCase,
                customerWebMapper
        );
    }

    @Test
    void createsACustomerAndReturnsCreatedWithItsDetails() {
        CreateCustomerRequest request = createRequest();
        Customer customer = customer(CUSTOMER_ID);
        CustomerResponse responseBody = response(CUSTOMER_ID);
        when(customerWebMapper.mapCreateRequestToDomain(request)).thenReturn(customer);
        when(createCustomerUseCase.createCustomer(customer)).thenReturn(customer);
        when(customerWebMapper.mapDomainToResponse(customer)).thenReturn(responseBody);

        ResponseEntity<CustomerResponse> response = controller.createCustomer(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(createCustomerUseCase).createCustomer(customer);
    }

    @Test
    void returnsCustomerAndOrdersWhenTheCustomerExists() {
        Customer customer = customer(CUSTOMER_ID);
        CustomWhitOrders customerWithOrders = new CustomWhitOrders(customer, List.of());
        CustomerWithOrdersResponse responseBody = new CustomerWithOrdersResponse(
                CUSTOMER_ID, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10", List.of()
        );
        when(getCustomerByIdUseCase.getCustomerById(CUSTOMER_ID)).thenReturn(customerWithOrders);
        when(customerWebMapper.mapDomainToResponse(customerWithOrders)).thenReturn(responseBody);

        ResponseEntity<CustomerWithOrdersResponse> response = controller.getCustomerById(CUSTOMER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(getCustomerByIdUseCase).getCustomerById(CUSTOMER_ID);
    }

    @Test
    void updatesTheCustomerAndReturnsTheUpdatedDetails() {
        UpdateCustomerRequest request = updateRequest();
        Customer customer = customer(CUSTOMER_ID);
        CustomerResponse responseBody = response(CUSTOMER_ID);
        when(customerWebMapper.mapUpdateRequestToDomain(CUSTOMER_ID, request)).thenReturn(customer);
        when(updateCustomerUseCase.updateCustomer(customer, CUSTOMER_ID)).thenReturn(customer);
        when(customerWebMapper.mapDomainToResponse(customer)).thenReturn(responseBody);

        ResponseEntity<CustomerResponse> response = controller.updateCustomer(CUSTOMER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(updateCustomerUseCase).updateCustomer(customer, CUSTOMER_ID);
    }

    @Test
    void deletesAnExistingCustomerAndReturnsNoContent() {
        ResponseEntity<Void> response = controller.deleteCustomer(CUSTOMER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCustomerUseCase).deleteCustomer(CUSTOMER_ID);
    }

    @Test
    void returnsAllCustomersWithTheirOrders() {
        List<CustomWhitOrders> customers = List.of(
                new CustomWhitOrders(customer(CUSTOMER_ID), List.of())
        );
        List<CustomerWithOrdersResponse> responseBody = List.of(
                new CustomerWithOrdersResponse(
                        CUSTOMER_ID, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10", List.of()
                )
        );
        when(getAllCustomersUseCase.getAllCustomers()).thenReturn(customers);
        when(customerWebMapper.mapDomainListToResponse(customers)).thenReturn(responseBody);

        ResponseEntity<List<CustomerWithOrdersResponse>> response = controller.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(getAllCustomersUseCase).getAllCustomers();
        verify(customerWebMapper).mapDomainListToResponse(customers);
    }

    @Test
    void returnsAnEmptyListWhenThereAreNoCustomers() {
        when(getAllCustomersUseCase.getAllCustomers()).thenReturn(List.of());
        when(customerWebMapper.mapDomainListToResponse(List.of())).thenReturn(List.of());

        ResponseEntity<List<CustomerWithOrdersResponse>> response = controller.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void passesErrorsWhileLoadingAllCustomersBackToTheExceptionHandler() {
        IllegalStateException expected = new IllegalStateException("Could not load customers");
        when(getAllCustomersUseCase.getAllCustomers()).thenThrow(expected);

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> controller.getAllCustomers()
        );

        assertSame(expected, actual);
        verify(customerWebMapper, never()).mapDomainListToResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void appliesAPatchAndReturnsTheUpdatedCustomer() {
        PatchCustomerRequest request = new PatchCustomerRequest(
                "Alexandra", null, null, null, null
        );
        PatchCustomerData patchData = new PatchCustomerData("Alexandra", null, null, null, null);
        Customer patchedCustomer = new Customer(
                CUSTOMER_ID, "Alexandra", "Lopez", "Diaz", "alex@example.com", "Main Street 10"
        );
        CustomerResponse responseBody = response(CUSTOMER_ID);
        when(customerWebMapper.mapPatchRequestToData(request)).thenReturn(patchData);
        when(patchCustomerUseCase.patchCustomer(CUSTOMER_ID, patchData)).thenReturn(patchedCustomer);
        when(customerWebMapper.mapDomainToResponse(patchedCustomer)).thenReturn(responseBody);

        ResponseEntity<CustomerResponse> response = controller.patchCustomer(CUSTOMER_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(patchCustomerUseCase).patchCustomer(CUSTOMER_ID, patchData);
    }

    @Test
    void passesPatchFailuresBackToTheExceptionHandler() {
        PatchCustomerRequest request = new PatchCustomerRequest("Alexandra", null, null, null, null);
        PatchCustomerData patchData = new PatchCustomerData("Alexandra", null, null, null, null);
        CustomerNotFoundException expected = new CustomerNotFoundException(CUSTOMER_ID);
        when(customerWebMapper.mapPatchRequestToData(request)).thenReturn(patchData);
        when(patchCustomerUseCase.patchCustomer(CUSTOMER_ID, patchData)).thenThrow(expected);

        CustomerNotFoundException actual = assertThrows(
                CustomerNotFoundException.class,
                () -> controller.patchCustomer(CUSTOMER_ID, request)
        );

        assertSame(expected, actual);
        verify(customerWebMapper, never()).mapDomainToResponse(org.mockito.ArgumentMatchers.any(Customer.class));
    }

    @Test
    void passesAnEmptyPatchBackAsAClientError() {
        PatchCustomerRequest request = new PatchCustomerRequest(null, null, null, null, null);
        PatchCustomerData patchData = new PatchCustomerData(null, null, null, null, null);
        EmptyCustomerPatchException expected = new EmptyCustomerPatchException();
        when(customerWebMapper.mapPatchRequestToData(request)).thenReturn(patchData);
        when(patchCustomerUseCase.patchCustomer(CUSTOMER_ID, patchData)).thenThrow(expected);

        EmptyCustomerPatchException actual = assertThrows(
                EmptyCustomerPatchException.class,
                () -> controller.patchCustomer(CUSTOMER_ID, request)
        );

        assertSame(expected, actual);
    }

    @Test
    void passesCreateConflictsBackToTheExceptionHandler() {
        CreateCustomerRequest request = createRequest();
        Customer customer = customer(CUSTOMER_ID);
        CustomerAlreadyExistsException expected = new CustomerAlreadyExistsException(CUSTOMER_ID);
        when(customerWebMapper.mapCreateRequestToDomain(request)).thenReturn(customer);
        when(createCustomerUseCase.createCustomer(customer)).thenThrow(expected);

        CustomerAlreadyExistsException actual = assertThrows(
                CustomerAlreadyExistsException.class,
                () -> controller.createCustomer(request)
        );

        assertSame(expected, actual);
        verify(customerWebMapper, never()).mapDomainToResponse(customer);
    }

    @Test
    void passesNotFoundErrorsBackFromReadUpdateAndDeleteOperations() {
        CustomerNotFoundException expected = new CustomerNotFoundException(CUSTOMER_ID);
        UpdateCustomerRequest request = updateRequest();
        when(getCustomerByIdUseCase.getCustomerById(CUSTOMER_ID)).thenThrow(expected);
        when(customerWebMapper.mapUpdateRequestToDomain(CUSTOMER_ID, request))
                .thenReturn(customer(CUSTOMER_ID));
        when(updateCustomerUseCase.updateCustomer(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(CUSTOMER_ID)
        )).thenThrow(expected);
        org.mockito.Mockito.doThrow(expected).when(deleteCustomerUseCase).deleteCustomer(CUSTOMER_ID);

        assertSame(expected, assertThrows(
                CustomerNotFoundException.class,
                () -> controller.getCustomerById(CUSTOMER_ID)
        ));
        assertSame(expected, assertThrows(
                CustomerNotFoundException.class,
                () -> controller.updateCustomer(CUSTOMER_ID, request)
        ));
        assertSame(expected, assertThrows(
                CustomerNotFoundException.class,
                () -> controller.deleteCustomer(CUSTOMER_ID)
        ));
    }

    private CreateCustomerRequest createRequest() {
        return new CreateCustomerRequest(
                CUSTOMER_ID, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10"
        );
    }

    private UpdateCustomerRequest updateRequest() {
        return new UpdateCustomerRequest("Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }

    private Customer customer(String id) {
        return new Customer(id, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }

    private CustomerResponse response(String id) {
        return new CustomerResponse(id, "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10");
    }
}
