package com.mx.lv.ordermanagment.adapter.in.web.spring.controller.v1;


import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.controller.OrdersController;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.OrderResponseMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.OrderSearchResponseMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderItemResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchResponse;
import com.mx.lv.ordermanagment.application.exception.OrderNotFoundException;
import com.mx.lv.ordermanagment.application.port.in.GetOrderByReferenceUseCase;
import com.mx.lv.ordermanagment.application.port.in.SearchOrdersUseCase;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrdersControllerTest {

    @Test
    void returnsSearchResultsAndPassesTheTextToTheUseCaseAndMapper() {
        SearchOrdersUseCase useCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderByReferenceUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper orderResponseMapper = mock(OrderResponseMapper.class);
        OrderSearchResponseMapper orderSearchResponseMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(useCase, getOrderByReferenceUseCase,
                orderResponseMapper, orderSearchResponseMapper);
        List<Order> orders = List.of(new Order(
                "order-1", "user-1", "WEB", "PENDIENTE", "Tienda Central", List.of()
        ));
        List<OrderSearchResponse> searchResults = List.of(
                new OrderSearchResponse("order-1", "WEB", "PENDIENTE", "Tienda Central", List.of())
        );
        when(useCase.searchOrders("tienda")).thenReturn(orders);
        when(orderSearchResponseMapper.mapOrdersToResponse(orders)).thenReturn(searchResults);

        ResponseEntity<List<OrderSearchResponse>> response = controller.searchOrders("tienda");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(searchResults, response.getBody());
        verify(useCase).searchOrders("tienda");
        verify(orderSearchResponseMapper).mapOrdersToResponse(orders);
    }

    @Test
    void returnsAnEmptyListWhenThereAreNoMatchingOrders() {
        SearchOrdersUseCase useCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderByReferenceUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper orderResponseMapper = mock(OrderResponseMapper.class);
        OrderSearchResponseMapper orderSearchResponseMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(useCase, getOrderByReferenceUseCase,
                orderResponseMapper, orderSearchResponseMapper);
        when(useCase.searchOrders("not found")).thenReturn(List.of());
        when(orderSearchResponseMapper.mapOrdersToResponse(List.of())).thenReturn(List.of());

        ResponseEntity<List<OrderSearchResponse>> response = controller.searchOrders("not found");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
        verify(orderSearchResponseMapper).mapOrdersToResponse(List.of());
    }

    @Test
    void propagatesUseCaseFailuresForTheGlobalHandlerToProcess() {
        SearchOrdersUseCase useCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderByReferenceUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper orderResponseMapper = mock(OrderResponseMapper.class);
        OrderSearchResponseMapper orderSearchResponseMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(useCase, getOrderByReferenceUseCase,
                orderResponseMapper, orderSearchResponseMapper);
        RuntimeException expected = new RuntimeException("search failed");
        when(useCase.searchOrders("tienda")).thenThrow(expected);

        RuntimeException actual = assertThrows(
                RuntimeException.class,
                () -> controller.searchOrders("tienda")
        );

        assertSame(expected, actual);
        verify(orderSearchResponseMapper, never()).mapOrdersToResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void returnsTheRequestedOrderWithItsItems() {
        SearchOrdersUseCase searchUseCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper mapper = mock(OrderResponseMapper.class);

        OrderSearchResponseMapper searchMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(searchUseCase, getOrderUseCase, mapper, searchMapper);
        Order order = new Order("ORD-1042", "user-1", "WEB", "EN PROCESO",
                "Tienda Central", List.of());
        OrderResponse responseBody = new OrderResponse("ORD-1042", "WEB", "EN PROCESO",
                List.of(new OrderItemResponse("ITEM-1", 2)));
        when(getOrderUseCase.getOrdersByReference("ORD-1042")).thenReturn(order);
        when(mapper.mapDomainOrderToOrderResponse(order)).thenReturn(responseBody);

        ResponseEntity<OrderResponse> response = controller.getOrderByReference("ORD-1042");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
        verify(getOrderUseCase).getOrdersByReference("ORD-1042");
        verify(mapper).mapDomainOrderToOrderResponse(order);
    }

    @Test
    void passesOnNotFoundWhenTheRequestedOrderDoesNotExist() {
        SearchOrdersUseCase searchUseCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper mapper = mock(OrderResponseMapper.class);
        OrderSearchResponseMapper searchMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(searchUseCase, getOrderUseCase, mapper, searchMapper);
        OrderNotFoundException expected = new OrderNotFoundException("ORD-MISSING");
        when(getOrderUseCase.getOrdersByReference("ORD-MISSING")).thenThrow(expected);

        OrderNotFoundException actual = assertThrows(
                OrderNotFoundException.class,
                () -> controller.getOrderByReference("ORD-MISSING")
        );

        assertSame(expected, actual);
        verify(mapper, never()).mapDomainOrderToOrderResponse(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void passesOnUnexpectedErrorsSoTheGlobalHandlerCanHandleThem() {
        SearchOrdersUseCase searchUseCase = mock(SearchOrdersUseCase.class);
        GetOrderByReferenceUseCase getOrderUseCase = mock(GetOrderByReferenceUseCase.class);
        OrderResponseMapper mapper = mock(OrderResponseMapper.class);
        OrderSearchResponseMapper searchMapper = mock(OrderSearchResponseMapper.class);
        OrdersController controller = new OrdersController(searchUseCase, getOrderUseCase, mapper, searchMapper);
        RuntimeException expected = new RuntimeException("Unexpected problem");
        when(getOrderUseCase.getOrdersByReference("ORD-1042")).thenThrow(expected);

        RuntimeException actual = assertThrows(
                RuntimeException.class,
                () -> controller.getOrderByReference("ORD-1042")
        );

        assertSame(expected, actual);
    }
}
