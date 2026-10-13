package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.Item;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchOrdersServiceTest {

    @Mock
    private LoadOrdersPort loadOrdersPort;

    private SearchOrdersService service;

    @BeforeEach
    void setUp() {
        service = new SearchOrdersService(loadOrdersPort);
    }

    @Test
    void returnsEmptyWithoutLoadingOrdersForNullOrBlankSearchText() {
        assertEquals(List.of(), service.searchOrders(null));
        assertEquals(List.of(), service.searchOrders(" \t\n "));

        verify(loadOrdersPort, never()).loadOrders();
    }

    @Test
    void searchesOrderReferenceAndStatusCaseInsensitively() {
        Order matchingOrder = order("AB-123", "EN PROCESO", "Tienda Central");
        Order otherOrder = order("ZX-456", "ENTREGADO", "Sucursal Norte");
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(matchingOrder, otherOrder));

        assertEquals(List.of(matchingOrder), service.searchOrders("ab 123"));
        assertEquals(List.of(matchingOrder), service.searchOrders("proceso"));
    }

    @Test
    void normalizesAccentsPunctuationAndWhitespaceForStoreSearch() {
        Order matchingOrder = order("1", "PENDIENTE", "Café-Mercado Central");
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(matchingOrder));

        assertEquals(List.of(matchingOrder), service.searchOrders("  CAFE   MERCADO "));
    }

    @Test
    void matchesStoreNamesWithOneSpellingError() {
        Order matchingOrder = order("1", "PENDIENTE", "Tienda Palacio");
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(matchingOrder));

        assertEquals(List.of(matchingOrder), service.searchOrders("palacioo"));
        assertEquals(List.of(), service.searchOrders("palaxioo"));
    }

    @Test
    void searchesItemDisplayNamesWithFlexibleMatching() {
        Item item = new Item("item-1", "sku-1", 2, "Audífonos inalámbricos", "ENVIADO");
        Order matchingOrder = new Order("1", "user-1", "WEB", "PENDIENTE", "Tienda", List.of(item));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(matchingOrder));

        assertEquals(List.of(matchingOrder), service.searchOrders("audifonos inalambrico"));
        assertEquals(List.of(), service.searchOrders("televisor"));
    }

    @Test
    void safelyIgnoresNullTextFieldsAndReturnsNoMatch() {
        Order orderWithNullFields = new Order("1", "user-1", "WEB", null, null,
                List.of(new Item("item-1", "sku-1", 1, null, "PENDIENTE")));
        when(loadOrdersPort.loadOrders()).thenReturn(List.of(orderWithNullFields));

        assertEquals(List.of(), service.searchOrders("inexistente"));
    }

    @Test
    void propagatesOrdersUnavailableExceptionFromThePort() {
        OrdersUnavailableException expected =
                new OrdersUnavailableException("orders unavailable");
        when(loadOrdersPort.loadOrders()).thenThrow(expected);

        OrdersUnavailableException actual = assertThrows(
                OrdersUnavailableException.class,
                () -> service.searchOrders("tienda")
        );

        assertSame(expected, actual);
    }

    private Order order(String reference, String status, String storeName) {
        return new Order(reference, "user-1", "WEB", status, storeName, List.of());
    }
}
