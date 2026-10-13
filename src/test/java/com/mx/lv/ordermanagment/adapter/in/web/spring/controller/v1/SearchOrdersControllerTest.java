package com.mx.lv.ordermanagment.adapter.in.web.spring.controller.v1;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchOrdersControllerTest {

    @Test
    void returnsOkWithTheSearchResultsAndPassesTheQueryToTheUseCase() {
        SearchOrdersUseCase useCase = mock(SearchOrdersUseCase.class);
        SearchOrdersController controller = new SearchOrdersController(useCase);
        List<Order> orders = List.of(new Order(
                "order-1", "user-1", "WEB", "PENDIENTE", "Tienda Central", List.of()
        ));
        when(useCase.searchOrders("tienda")).thenReturn(orders);

        ResponseEntity<List<Order>> response = controller.searchOrders("tienda");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(orders, response.getBody());
        verify(useCase).searchOrders("tienda");
    }

    @Test
    void propagatesUseCaseFailuresForTheGlobalHandlerToProcess() {
        SearchOrdersUseCase useCase = mock(SearchOrdersUseCase.class);
        SearchOrdersController controller = new SearchOrdersController(useCase);
        RuntimeException expected = new RuntimeException("search failed");
        when(useCase.searchOrders("tienda")).thenThrow(expected);

        RuntimeException actual = assertThrows(
                RuntimeException.class,
                () -> controller.searchOrders("tienda")
        );

        assertSame(expected, actual);
    }
}
