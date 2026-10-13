package com.mx.lv.ordermanagment.adapter.in.web.spring.controller.v1;

import com.mx.lv.ordermanagment.application.port.in.SearchOrdersUseCase;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class SearchOrdersController {

    private final SearchOrdersUseCase searchOrdersUseCase;

    public SearchOrdersController(SearchOrdersUseCase searchOrdersUseCase) {
        this.searchOrdersUseCase = searchOrdersUseCase;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Order>> searchOrders(@RequestParam(name = "text") String text) {
        List<Order> orders = searchOrdersUseCase.searchOrders(text);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }
}
