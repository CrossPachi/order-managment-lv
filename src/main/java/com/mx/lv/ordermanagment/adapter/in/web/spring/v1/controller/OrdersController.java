package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.controller;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.OrderResponseMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.OrderSearchResponseMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderResponse;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.response.OrderSearchResponse;
import com.mx.lv.ordermanagment.application.port.in.GetOrderByReferenceUseCase;
import com.mx.lv.ordermanagment.application.port.in.SearchOrdersUseCase;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final SearchOrdersUseCase searchOrdersUseCase;

    private final GetOrderByReferenceUseCase getOrderByReferenceUseCase;

    private final OrderResponseMapper orderResponseMapper;

    private final OrderSearchResponseMapper orderSearchResponseMapper;

    public OrdersController(SearchOrdersUseCase searchOrdersUseCase,
                            GetOrderByReferenceUseCase getOrderByReferenceUseCase,
                            OrderResponseMapper orderResponseMapper,
                            OrderSearchResponseMapper orderSearchResponseMapper) {
        this.searchOrdersUseCase = searchOrdersUseCase;
        this.getOrderByReferenceUseCase = getOrderByReferenceUseCase;
        this.orderResponseMapper = orderResponseMapper;
        this.orderSearchResponseMapper = orderSearchResponseMapper;
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderSearchResponse>> searchOrders(@RequestParam(name = "text") String text) {
        List<Order> orders = searchOrdersUseCase.searchOrders(text);
        List<OrderSearchResponse> response = orderSearchResponseMapper.mapOrdersToResponse(orders);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{orderRef}")
    public ResponseEntity<OrderResponse> getOrderByReference(@PathVariable(name = "orderRef") String reference) {
        Order orderDomain = getOrderByReferenceUseCase.getOrdersByReference(reference);
        OrderResponse orderResponse = orderResponseMapper.mapDomainOrderToOrderResponse(orderDomain);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
