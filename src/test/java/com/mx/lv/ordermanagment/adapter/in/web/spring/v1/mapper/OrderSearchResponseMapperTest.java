package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchItemResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchResponse;
import com.mx.lv.ordermanagment.domain.model.Item;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderSearchResponseMapperTest {

    private final OrderSearchResponseMapper mapper = new OrderSearchResponseMapper();

    @Test
    void mapsOrderDetailsAndEachItemForSearchResults() {
        List<Order> orders = List.of(
                new Order(
                        "ORD-1042",
                        "user-1",
                        "WEB",
                        "EN PROCESO",
                        "Tienda Centro",
                        List.of(
                                new Item("ITEM-1", "SKU-1", 2, "Audífonos", "ENVIADO"),
                                new Item("ITEM-2", "SKU-2", 1, "Cargador", "PENDIENTE")
                        )
                ),
                new Order(
                        "ORD-2042",
                        "user-2",
                        "APP",
                        "ENTREGADO",
                        "Tienda Norte",
                        List.of(new Item("ITEM-3", "SKU-3", 4, "Cable USB", "ENTREGADO"))
                )
        );

        List<OrderSearchResponse> response = mapper.mapOrdersToResponse(orders);

        assertEquals(List.of(
                new OrderSearchResponse(
                        "ORD-1042",
                        "WEB",
                        "EN PROCESO",
                        "Tienda Centro",
                        List.of(
                                new OrderSearchItemResponse("ITEM-1", 2, "Audífonos"),
                                new OrderSearchItemResponse("ITEM-2", 1, "Cargador")
                        )
                ),
                new OrderSearchResponse(
                        "ORD-2042",
                        "APP",
                        "ENTREGADO",
                        "Tienda Norte",
                        List.of(new OrderSearchItemResponse("ITEM-3", 4, "Cable USB"))
                )
        ), response);
    }

    @Test
    void returnsNoResultsWhenThereAreNoOrdersToMap() {
        assertEquals(List.of(), mapper.mapOrdersToResponse(List.of()));
    }

    @Test
    void keepsOrdersWithNoItemsAndReturnsAnUnmodifiableResult() {
        Order orderWithoutItems =
                new Order("ORD-1042", "user-1", "WEB", "EN PROCESO", "Tienda Centro", List.of());

        List<OrderSearchResponse> response = mapper.mapOrdersToResponse(List.of(orderWithoutItems));

        assertEquals(List.of(new OrderSearchResponse(
                "ORD-1042", "WEB", "EN PROCESO", "Tienda Centro", List.of()
        )), response);
        assertThrows(UnsupportedOperationException.class, response::clear);
    }
}
