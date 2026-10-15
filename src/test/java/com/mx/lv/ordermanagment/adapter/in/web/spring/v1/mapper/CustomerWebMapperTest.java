package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.CreateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.UpdateCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.CustomerWithOrdersResponse;
import com.mx.lv.ordermanagment.domain.model.CustomWhitOrders;
import com.mx.lv.ordermanagment.domain.model.Customer;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerWebMapperTest {

    private final CustomerWebMapper mapper = new CustomerWebMapper();

    @Test
    void mapsCreateRequestToCustomerWithoutChangingItsFields() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10"
        );

        assertEquals(customer("user-123"), mapper.mapCreateRequestToDomain(request));
    }

    @Test
    void usesThePathIdWhenMappingAnUpdateRequest() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10"
        );

        assertEquals(customer("path-user-id"), mapper.mapUpdateRequestToDomain("path-user-id", request));
    }

    @Test
    void mapsCustomerFieldsToTheCreateAndUpdateResponse() {
        Customer customer = customer("user-123");

        assertEquals(new CustomerResponse(
                "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10"
        ), mapper.mapDomainToResponse(customer));
    }

    @Test
    void includesOrderReferencesInTheCustomerDetailsResponse() {
        CustomWhitOrders customerWithOrders = new CustomWhitOrders(
                customer("user-123"),
                List.of(order("ORD-1"), order("ORD-2"))
        );

        assertEquals(new CustomerWithOrdersResponse(
                "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10",
                List.of("ORD-1", "ORD-2")
        ), mapper.mapDomainToResponse(customerWithOrders));
    }

    @Test
    void returnsNoOrderReferencesWhenTheCustomerHasNoOrders() {
        CustomWhitOrders customerWithOrders = new CustomWhitOrders(customer("user-123"), List.of());

        assertEquals(List.of(), mapper.mapDomainToResponse(customerWithOrders).orders());
    }

    @Test
    void mapsAListOfCustomersAndKeepsEachCustomersOrderReferences() {
        List<CustomWhitOrders> customers = List.of(
                new CustomWhitOrders(customer("user-123"), List.of(order("ORD-1"), order("ORD-2"))),
                new CustomWhitOrders(customer("user-456"), List.of())
        );

        List<CustomerWithOrdersResponse> response = mapper.mapDomainListToResponse(customers);

        assertEquals(List.of(
                new CustomerWithOrdersResponse(
                        "user-123", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10",
                        List.of("ORD-1", "ORD-2")
                ),
                new CustomerWithOrdersResponse(
                        "user-456", "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10", List.of()
                )
        ), response);
    }

    @Test
    void mapsAnEmptyCustomerListToAnEmptyResponseList() {
        assertEquals(List.of(), mapper.mapDomainListToResponse(List.of()));
    }

    @Test
    void responseOrderReferencesCannotBeChangedAfterMapping() {
        CustomWhitOrders customerWithOrders = new CustomWhitOrders(
                customer("user-123"), List.of(order("ORD-1"))
        );

        CustomerWithOrdersResponse response = mapper.mapDomainToResponse(customerWithOrders);

        assertThrows(UnsupportedOperationException.class, response.orders()::clear);
    }

    private Customer customer(String id) {
        return new Customer(id, "Ana", "Pérez", "Gómez", "ana@example.com", "Main Street 10");
    }

    private Order order(String reference) {
        return new Order(reference, "user-123", "WEB", "PROCESSING", "Store", List.of());
    }
}
