package com.mx.lv.ordermanagment.configuration.spring;

import com.mx.lv.ordermanagment.adapter.out.http.MockApiOrdersAdapter;
import com.mx.lv.ordermanagment.application.port.in.SearchOrdersUseCase;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.application.usecase.SearchOrdersService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OrderSearchConfiguration {

    @Bean
    public LoadOrdersPort loadOrdersPort(RestClient mockApiRestClient) {
        return new MockApiOrdersAdapter(mockApiRestClient);
    }

    @Bean
    public SearchOrdersUseCase searchOrdersUseCase(
            LoadOrdersPort loadOrdersPort
    ) {
        return new SearchOrdersService(loadOrdersPort);
    }
}