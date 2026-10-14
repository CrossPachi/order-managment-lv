package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchItemResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchResponse;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderSearchResponseMapper {

    public List<OrderSearchResponse> mapOrdersToResponse(List<Order> orders) {
        return orders.stream()
                .map(order -> mapOrderDomainToResponse(order))
                .toList();
    }

    private OrderSearchResponse mapOrderDomainToResponse(Order order) {
        List<OrderSearchItemResponse> items = order.items().stream()
                .map(item -> new OrderSearchItemResponse(
                        item.itemId(),
                        item.quantity(),
                        item.displayName()
                ))
                .toList();
        return new OrderSearchResponse(
                order.orderRef(),
                order.channel(),
                order.orderStatus(),
                order.storeName(),
                items
        );
    }
}
