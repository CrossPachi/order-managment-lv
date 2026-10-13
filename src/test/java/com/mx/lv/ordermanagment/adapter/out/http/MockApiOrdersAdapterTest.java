package com.mx.lv.ordermanagment.adapter.out.http;

import com.mx.lv.ordermanagment.adapter.out.http.dto.MockApiItemResponse;
import com.mx.lv.ordermanagment.adapter.out.http.dto.MockApiOrderResponse;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import com.mx.lv.ordermanagment.domain.model.Item;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class MockApiOrdersAdapterTest {

    @Test
    void mapsOrdersAndItemsAndSkipsUnknownItemReferences() {
        MockApiOrderResponse externalOrder = new MockApiOrderResponse(
                "order-1", "user-1", "WEB", "PENDIENTE", false, false,
                List.of("item-1", "missing-item"), "Tienda Central", "external-order-id"
        );
        MockApiItemResponse externalItem = new MockApiItemResponse(
                "item-1", "sku-1", 3, "Audífonos", "ENVIADO", "external-item-id"
        );
        RestClient restClient = mockRestClient(List.of(externalOrder), List.of(externalItem));

        List<Order> result = new MockApiOrdersAdapter(restClient).loadOrders();

        assertEquals(List.of(new Order(
                "order-1",
                "user-1",
                "WEB",
                "PENDIENTE",
                "Tienda Central",
                List.of(new Item("item-1", "sku-1", 3, "Audífonos", "ENVIADO"))
        )), result);
    }

    @Test
    void returnsEmptyWhenTheExternalOrdersListIsEmpty() {
        RestClient restClient = mockRestClient(List.of(), List.of());

        assertEquals(List.of(), new MockApiOrdersAdapter(restClient).loadOrders());
    }

    @Test
    void throwsUnavailableWhenOrdersResponseBodyIsNull() {
        RestClient restClient = mockRestClient(null, List.of());

        OrdersUnavailableException exception = assertThrows(
                OrdersUnavailableException.class,
                () -> new MockApiOrdersAdapter(restClient).loadOrders()
        );

        assertEquals("External orders service returned an empty body", exception.getMessage());
    }

    @Test
    void throwsUnavailableWhenItemsResponseBodyIsNull() {
        MockApiOrderResponse externalOrder = new MockApiOrderResponse(
                "order-1", "user-1", "WEB", "PENDIENTE", false, false,
                List.of(), "Tienda Central", "external-order-id"
        );
        RestClient restClient = mockRestClient(List.of(externalOrder), null);

        OrdersUnavailableException exception = assertThrows(
                OrdersUnavailableException.class,
                () -> new MockApiOrdersAdapter(restClient).loadOrders()
        );

        assertEquals("External items service returned an empty body", exception.getMessage());
    }

    @Test
    void wrapsRestClientFailuresAndPreservesTheCause() {
        RestClient restClient = mockRestClient(List.of(), List.of());
        RestClient.ResponseSpec ordersResponse = mock(RestClient.ResponseSpec.class);
        RestClient.RequestHeadersUriSpec<?> request = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> ordersRequest = mock(RestClient.RequestHeadersSpec.class);
        doReturn(request).when(restClient).get();
        doReturn(ordersRequest).when(request).uri("/pedidos");
        doReturn(ordersResponse).when(ordersRequest).retrieve();
        RestClientException cause = new RestClientException("remote service failed");
        doThrow(cause).when(ordersResponse).body(any(ParameterizedTypeReference.class));

        OrdersUnavailableException exception = assertThrows(
                OrdersUnavailableException.class,
                () -> new MockApiOrdersAdapter(restClient).loadOrders()
        );

        assertEquals("Unable to load orders from external services", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    private RestClient mockRestClient(Object ordersBody, Object itemsBody) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec<?> request = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> ordersRequest = mock(RestClient.RequestHeadersSpec.class);
        RestClient.RequestHeadersSpec<?> itemsRequest = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec ordersResponse = mock(RestClient.ResponseSpec.class);
        RestClient.ResponseSpec itemsResponse = mock(RestClient.ResponseSpec.class);

        doReturn(request).when(restClient).get();
        doReturn(ordersRequest).when(request).uri("/pedidos");
        doReturn(itemsRequest).when(request).uri("/items");
        doReturn(ordersResponse).when(ordersRequest).retrieve();
        doReturn(itemsResponse).when(itemsRequest).retrieve();
        doReturn(ordersBody).when(ordersResponse).body(any(ParameterizedTypeReference.class));
        doReturn(itemsBody).when(itemsResponse).body(any(ParameterizedTypeReference.class));

        return restClient;
    }
}
