package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderItemResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderResponse;
import com.mx.lv.ordermanagment.domain.model.Item;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderResponseMapperTest {

    private final OrderResponseMapper mapper = new OrderResponseMapper();

    @Test
    void keepsTheOrderDetailsAndOnlyTheItemFieldsShownInTheResponse() {
        Order order = new Order(
                "ORD-2042",
                "user-1",
                "WEB",
                "EN PROCESO",
                "Tienda Central",
                List.of(
                        new Item("ITEM-1", "SKU-1", 2, "Audífonos", "ENVIADO"),
                        new Item("ITEM-2", "SKU-2", 1, "Cargador", "PENDIENTE")
                )
        );

        OrderResponse response = mapper.mapDomainOrderToOrderResponse(order);

        assertEquals(new OrderResponse(
                "ORD-2042",
                "WEB",
                "EN PROCESO",
                List.of(
                        new OrderItemResponse("ITEM-1", 2),
                        new OrderItemResponse("ITEM-2", 1)
                )
        ), response);
    }

    @Test
    void returnsAnOrderWithNoItemsWhenTheOrderHasNone() {
        Order order = new Order("ORD-2042", "user-1", "WEB", "EN PROCESO", "Tienda", List.of());

        OrderResponse response = mapper.mapDomainOrderToOrderResponse(order);

        assertEquals(new OrderResponse("ORD-2042", "WEB", "EN PROCESO", List.of()), response);
    }

    @Test
    void responseDoesNotAllowItsItemsToBeChangedAfterMapping() {
        Order order = new Order(
                "ORD-2042", "user-1", "WEB", "EN PROCESO", "Tienda",
                List.of(new Item("ITEM-1", "SKU-1", 2, "Audífonos", "ENVIADO"))
        );
        OrderResponse response = mapper.mapDomainOrderToOrderResponse(order);

        assertThrows(UnsupportedOperationException.class, () -> response.items().clear());
    }
}
