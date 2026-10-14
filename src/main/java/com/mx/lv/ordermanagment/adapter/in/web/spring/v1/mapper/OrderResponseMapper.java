package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderItemResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderResponse;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderResponseMapper {

    public OrderResponse mapDomainOrderToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.items().stream()
                .map(item -> new OrderItemResponse(
                        item.itemId(),
                        item.quantity()
                ))
                .toList();

        return new OrderResponse(
                order.orderRef(),
                order.channel(),
                order.orderStatus(),
                items
        );
    }
}
