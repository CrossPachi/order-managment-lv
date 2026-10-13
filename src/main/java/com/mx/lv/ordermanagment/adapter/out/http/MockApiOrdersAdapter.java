package com.mx.lv.ordermanagment.adapter.out.http;

import com.mx.lv.ordermanagment.adapter.out.http.dto.MockApiItemResponse;
import com.mx.lv.ordermanagment.adapter.out.http.dto.MockApiOrderResponse;
import com.mx.lv.ordermanagment.application.exception.OrdersUnavailableException;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.Item;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockApiOrdersAdapter implements LoadOrdersPort {

    private static final String ORDERS_PATH = "/pedidos";
    private static final String ITEMS_PATH = "/items";

    private final RestClient restClient;

    public MockApiOrdersAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<Order> loadOrders() {
        try {
            List<MockApiOrderResponse> externalOrders = loadExternalOrders();
            List<MockApiItemResponse> externalItems = loadExternalItems();

            Map<String, Item> mapItemsById = indexItemsById(externalItems);

            return externalOrders.stream()
                    .map(order -> mapToOrder(order, mapItemsById))
                    .toList();
        } catch (RestClientException exception) {
            throw new OrdersUnavailableException(
                    "Unable to load orders from external services",
                    exception
            );
        }
    }


    private List<MockApiOrderResponse> loadExternalOrders() {
        List<MockApiOrderResponse> orders = restClient
                .get()
                .uri(ORDERS_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (orders == null) {
            throw new OrdersUnavailableException(
                    "External orders service returned an empty body"
            );
        }

        return orders;
    }

    private List<MockApiItemResponse> loadExternalItems() {
        List<MockApiItemResponse> items = restClient
                .get()
                .uri(ITEMS_PATH)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (items == null) {
            throw new OrdersUnavailableException(
                    "External items service returned an empty body"
            );
        }

        return items;
    }

    private Map<String, Item> indexItemsById(List<MockApiItemResponse> externalItems) {
        return externalItems.stream()
                .map(externalItem -> mapToItem(externalItem))
                .collect(Collectors.toUnmodifiableMap(
                        item -> item.itemId(),
                        item -> item
                ));
    }

    private Item mapToItem(MockApiItemResponse externalItem) {
        return new Item(
                externalItem.itemId(),
                externalItem.skuId(),
                externalItem.quantity(),
                externalItem.displayName(),
                externalItem.deliveryStatus()
        );
    }

    private Order mapToOrder(MockApiOrderResponse externalOrder, Map<String, Item> mapItemsById) {
        List<Item> orderItems = externalOrder.items().stream()
                .map(itemId -> mapItemsById.get(itemId))
                .filter(Objects::nonNull)
                .toList();

        return new Order(
                externalOrder.orderRef(),
                externalOrder.userId(),
                externalOrder.canal(),
                externalOrder.orderStatus(),
                externalOrder.storeName(),
                orderItems
        );
    }
}